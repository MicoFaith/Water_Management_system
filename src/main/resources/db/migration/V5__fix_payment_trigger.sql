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
    SELECT COALESCE(SUM(p.amount_paid), 0), b.total_amount, b.customer_id, b.billing_month, b.billing_year
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

        INSERT INTO notifications (customer_id, bill_id, message, notification_type, created_at, is_read)
        VALUES (v_customer_id, NEW.bill_id, v_message, 'BILL_PAID', NOW(), FALSE);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
