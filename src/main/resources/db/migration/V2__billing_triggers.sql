CREATE OR REPLACE FUNCTION format_billing_period(p_month INTEGER, p_year INTEGER)
RETURNS TEXT AS $$
BEGIN
    RETURN TO_CHAR(TO_DATE(p_month::TEXT || '/' || p_year::TEXT, 'MM/YYYY'), 'Month YYYY');
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION notify_on_bill_generation()
RETURNS TRIGGER AS $$
DECLARE
    v_customer_name TEXT;
    v_period TEXT;
    v_message TEXT;
BEGIN
    SELECT full_names INTO v_customer_name FROM customers WHERE id = NEW.customer_id;
    v_period := TRIM(format_billing_period(NEW.billing_month, NEW.billing_year));
    v_message := 'Dear ' || v_customer_name || ', Your ' || v_period || ' utility bill of ' ||
                 NEW.total_amount || ' FRW has been successfully processed.';

    INSERT INTO notifications (customer_id, bill_id, message, notification_type, created_at)
    VALUES (NEW.customer_id, NEW.id, v_message, 'BILL_GENERATED', NOW());

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bill_generation_notification
    AFTER INSERT ON bills
    FOR EACH ROW
    EXECUTE FUNCTION notify_on_bill_generation();

CREATE OR REPLACE FUNCTION process_full_payment()
RETURNS TRIGGER AS $$
DECLARE
    v_total_paid NUMERIC(12, 2);
    v_bill_total NUMERIC(12, 2);
    v_customer_id BIGINT;
    v_customer_name TEXT;
    v_billing_month INTEGER;
    v_billing_year INTEGER;
    v_period TEXT;
    v_message TEXT;
BEGIN
    SELECT COALESCE(SUM(amount_paid), 0), b.total_amount, b.customer_id, b.billing_month, b.billing_year
    INTO v_total_paid, v_bill_total, v_customer_id, v_billing_month, v_billing_year
    FROM payments p
    JOIN bills b ON b.id = p.bill_id
    WHERE p.bill_id = NEW.bill_id
    GROUP BY b.total_amount, b.customer_id, b.billing_month, b.billing_year;

    IF v_total_paid >= v_bill_total THEN
        UPDATE bills
        SET status = 'PAID',
            amount_paid = v_total_paid,
            outstanding_balance = 0
        WHERE id = NEW.bill_id;

        SELECT full_names INTO v_customer_name FROM customers WHERE id = v_customer_id;
        v_period := TRIM(format_billing_period(v_billing_month, v_billing_year));
        v_message := 'Dear ' || v_customer_name || ', Your ' || v_period || ' utility bill of ' ||
                     v_bill_total || ' FRW has been successfully processed.';

        INSERT INTO notifications (customer_id, bill_id, message, notification_type, created_at)
        VALUES (v_customer_id, NEW.bill_id, v_message, 'BILL_PAID', NOW());
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_full_payment_notification
    AFTER INSERT ON payments
    FOR EACH ROW
    EXECUTE FUNCTION process_full_payment();
