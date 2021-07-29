SET FOREIGN_KEY_CHECKS=0;
insert ignore into stretchy_report values(Null, 'Customer Adjustments Date Wise Details', 'Table', '', 'Invoice&Collections', 
'SELECT 
    off.name AS officeName,
    clnt.id AS clientId,
    clnt.display_name AS clientName,
    DATE_FORMAT(adj.adjustment_date, ''%Y-%m-%d'') AS adjustmentDate,
    mcv.code_value as adjustmentCode,
    adjustment_type AS adjustmentType,
    SUM((CASE adjustment_type
        WHEN ''DEBIT'' THEN adjustment_amount
        ELSE NULL
    END)) AS debitAdjustment,
    SUM((CASE adjustment_type
        WHEN ''CREDIT'' THEN adjustment_amount
        ELSE NULL
    END)) AS creditAdjustment
from
    m_office off
        JOIN
    m_client clnt ON off.id = clnt.office_id
        JOIN
    b_adjustments adj ON clnt.id = adj.client_id
        JOIN
     m_code_value mcv ON mcv.id = adj.adjustment_code
WHERE (off.id = ''${officeId}'' OR -1 = ''${officeId}'') AND adj.adjustment_date BETWEEN ''${startDate}'' AND ''${endDate}''
GROUP BY clientId,adjustmentType,adjustmentDate,adjustmentCode ORDER BY clientId,adjustmentDate', 
'Customer Adjustments---Date wise details', '0', '1');

SET @id=(select id from stretchy_report where report_name='Customer Adjustments Date Wise Details');
SET @offId=(SELECT id FROM stretchy_parameter where parameter_label='Office');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');

SET @StartDateID=(SELECT id FROM stretchy_parameter where parameter_label='StartDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@StartDateID,'StartDate');

SET @DATEID=(SELECT id FROM stretchy_parameter where parameter_label='EndDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DATEID,'EndDate');


