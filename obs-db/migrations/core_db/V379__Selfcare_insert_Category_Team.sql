INSERT ignore INTO `b_team` (`user_id`, `team_code`, `team_description`, `team_category`, `status`, `is_deleted`) VALUES ('1', 'selfcare', 'SelfCare Team', '1', 'ACTIVE', 'N');

SET @t_tid=(select id from b_team where team_code='selfcare');
INSERT ignore INTO `b_team_user` (`team_id`, `user_id`, `is_deleted`) VALUES (@t_tid,'1','N');

SET @m_mid=(select id from m_code where code_name='Problem Code');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mid, 'SelfCare Problems', '4');

SET @sc_scid=(select id from m_code_value where code_value='SelfCare Problems');
INSERT ignore INTO `b_sub_category` (`main_category`, `sub_category`) VALUES (@sc_scid, 'selfcare1');

