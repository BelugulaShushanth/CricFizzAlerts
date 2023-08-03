package com.cricFizzAlerts.repository;

import com.cricFizzAlerts.bean.alert.AlertDetails;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertsRepository extends CassandraRepository<AlertDetails, String> {
}
