SET SQL_SAFE_UPDATES = 0;

update m_appuser set email='youradmin@mailid.com' where id in (0,1,2);


SET SQL_SAFE_UPDATES = 1;
