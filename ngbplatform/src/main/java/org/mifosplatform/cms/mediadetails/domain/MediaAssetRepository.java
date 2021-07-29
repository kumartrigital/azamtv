package org.mifosplatform.cms.mediadetails.domain;

import org.mifosplatform.cms.media.domain.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long>, JpaSpecificationExecutor<MediaAsset> {

	@Query("from MediaAsset media where media.title=:title")
	MediaAsset findOneByTitle(@Param("title") String title);

	@Query("from MediaAsset media where media.overview=:movieCode")
	MediaAsset findOneByOverView(@Param("movieCode") String movieCode);


}
