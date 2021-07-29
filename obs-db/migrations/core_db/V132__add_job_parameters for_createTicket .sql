SET @id=(select id from job where  name='SIMULATOR');
INSERT INTO job_parameters (job_id, param_name,param_type,param_default_value,param_value,is_dynamic,query_values) 
select @id, 'status', 'COMBO', NULL, 'updateStatus', 'Y', NULL from  dual
WHERE not exists (select 1 from job_parameters where param_name = 'status' and job_id = @id)
union all 
select @id, 'Ticket', 'COMBO', NULL, 'createTicket', 'Y', NULL from  dual
WHERE not exists (select 1 from job_parameters where param_name = 'Ticket' and job_id = @id);
