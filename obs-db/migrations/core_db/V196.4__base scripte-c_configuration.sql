UPDATE c_configuration SET enabled=1 where name = 'align-biiling-cycle';
UPDATE c_configuration SET enabled=1 where name = 'disconnection-credit';
UPDATE c_configuration SET enabled=1 where name = 'implicit-association';
UPDATE c_configuration SET enabled=1 where name = 'is-selfcareuser';
UPDATE c_configuration SET enabled=0 where name = 'change-plan-align-dates';

UPDATE b_eventaction_mapping SET is_deleted = 'N' where event_name = 'Order activation';
