SET SQL_SAFE_UPDATES = 0;

UPDATE `b_eventaction_mapping` SET `action_name`='Send Ticket Email' WHERE `id`='6';

UPDATE `b_eventaction_mapping` SET `action_name`='Add Comment Email' WHERE `event_name`='Add Comment';

UPDATE `b_eventaction_mapping` SET `action_name`='Close Ticket Email' WHERE `event_name`='Close Ticket';


SET SQL_SAFE_UPDATES = 1;
