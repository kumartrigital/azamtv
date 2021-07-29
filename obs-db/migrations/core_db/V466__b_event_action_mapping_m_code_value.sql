delete from m_code_value where code_id=28;

Insert m_code_value 
select distinct 0,28,event_name,0 from b_eventaction_mapping;



delete from m_code_value where code_id=29;

Insert m_code_value 
select distinct 0,29,action_name,0 from b_eventaction_mapping;
