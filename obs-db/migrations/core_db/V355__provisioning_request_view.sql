DROP view IF EXISTS  provisioning_requests_vw;


Create view provisioning_requests_vw as Select a.id,a.client_id,a.request_type,a.status,a.created_date,
b.response_message,b.response_status from b_provisioning_request a,
b_provisioning_request_detail b where a.id=b.provisioning_req_id ;



