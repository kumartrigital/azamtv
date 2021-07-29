package org.mifosplatform.organisation.channel.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChannelRepository extends JpaRepository<Channel, Long>,
JpaSpecificationExecutor<Channel>{

	

}
