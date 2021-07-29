CREATE INDEX idx_clid ON b_item_detail(client_id);
CREATE INDEX idx_itemdoid ON b_item_detail(office_id);
CREATE INDEX idx_itemmid ON b_item_detail(item_master_id);
CREATE INDEX idx_supid ON b_grn(supplier_id);

Drop view mvdiscount_vw;
Create view mvdiscount_vw as 
SELECT `m`.`id` AS `mediaId`
 ,`m`.`title` AS `title`
 ,`m`.`image` AS `image`
 ,`m`.`rating` AS `rating`
 ,`ed`.`event_id` AS `eventId`
 ,count(`ed`.`media_id`) AS `media_count` 
 FROM `b_media_asset` `m` 
 JOIN `b_mod_detail` `ed` ON (`ed`.`media_id` = `m`.`id`)
 JOIN `b_mod_master` `em` ON (`em`.`id` = `ed`.`event_id`)
 JOIN `b_mod_pricing` `ep` ON (`em`.`id` = `ep`.`event_id`)
WHERE (`ep`.`discount_id` >= 1)
GROUP BY `m`.`id` HAVING (count(DISTINCT `ed`.`event_id`) >= 1);


Drop view mvall_vw;
Create view mvall_vw as
SELECT `m`.`id` AS `mediaId`
 ,`m`.`title` AS `title`
 ,`m`.`image` AS `image`
 ,`m`.`rating` AS `rating`
 ,`ed`.`event_id` AS `eventId`
 ,count(`ed`.`media_id`) AS `media_count`
FROM `b_media_asset` `m` LEFT JOIN `b_mod_detail` `ed` ON (`ed`.`media_id` = `m`.`id`) 
 LEFT JOIN `b_mod_master` `em` ON (`em`.`id` = `ed`.`event_id`) 
 GROUP BY `m`.`id`,`m`.`title`;

Drop view mvhighrate_vw;
Create table mvhighrate_vw as
SELECT `m`.`id` AS `mediaId`
 ,`m`.`title` AS `title`
 ,`m`.`image` AS `image`
 ,`m`.`rating` AS `rating`
 ,`ed`.`event_id` AS `eventId`
 ,count(`ed`.`media_id`) AS `media_count`
FROM `b_media_asset` `m` LEFT JOIN `b_mod_detail` `ed` ON (`ed`.`media_id` = `m`.`id`) 
 LEFT JOIN `b_mod_master` `em` ON (`em`.`id` = `ed`.`event_id`) 
WHERE (`m`.`rating` >= 4.5)
GROUP BY `m`.`id`,`m`.`title`;


Drop view mvnewrelease_vw;
Create view mvnewrelease_vw as
SELECT `m`.`id` AS `mediaId`
 ,`m`.`title` AS `title`
 ,`m`.`image` AS `image`
 ,`m`.`rating` AS `rating`
 ,`ed`.`event_id` AS `eventId`
 ,count(`ed`.`media_id`) AS `media_count`
FROM `b_media_asset` `m` LEFT JOIN `b_mod_detail` `ed` ON (`ed`.`media_id` = `m`.`id`)
 LEFT JOIN `b_mod_master` `em` ON (`em`.`id` = `ed`.`event_id`) 
WHERE (`m`.`release_date` <= (now() + interval - (3) month))
GROUP BY `m`.`id`;

Drop view mvpromotion_vw ;

Create view mvpromotion_vw as 
SELECT `ed`.`event_id` AS `event_id`
 ,`ma`.`id` AS `mediaId`
 ,`ma`.`title` AS `title`
 ,`ma`.`image` AS `image`
 ,`ed`.`event_id` AS `eventId`
 ,`ma`.`rating` AS `rating`
FROM `b_media_asset` `ma` JOIN `b_mod_detail` `ed` ON (`ed`.`media_id` = `ma`.`id`) 
WHERE `ed`.`event_id` IN ( SELECT `emd`.`event_id` FROM `b_mod_master` `em` 
JOIN `b_mod_detail` `emd` ON (`em`.`id` = `emd`.`event_id`) 
GROUP BY `emd`.`event_id` HAVING (count(`emd`.`event_id`) > 1));


Drop view mvwatched_vw;

Create view mvwatched_vw as
SELECT `m`.`id` AS `mediaId`
 ,`m`.`title` AS `title`
 ,`m`.`image` AS `image`
 ,`m`.`rating` AS `rating`
 ,'W' AS `assetTag`
 ,`m`.`release_date` AS `release_date`
 ,`ed`.`event_id` AS `eventId`
 ,count(`eo`.`id`) AS `COUNT(eo.id)`
FROM `b_media_asset` `m` JOIN `b_mod_detail` `ed` ON (`m`.`id` = `ed`.`media_id`) 
JOIN `b_modorder` `eo` ON (`eo`.`event_id` = `ed`.`event_id`) 
ORDER BY 6 DESC;



