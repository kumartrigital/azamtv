Drop table if exists b_jv_transactions;
Create view b_jv_transactions as
Select jtd.id,jt.posting_date as jv_date,jtd.party_id as client_id,
case when type='Debit' then amount end as debit_amount,
case when type='Credit' then amount end as credit_amount,
CONCAT('Redeem ',jtd.account) remarks,
'1' as createdby_id
FROM b_jv_transaction jt
LEFT JOIN b_jv_transaction_details jtd ON jt.id=jtd.transaction_id
WHERE jtd.party_type='Client';
