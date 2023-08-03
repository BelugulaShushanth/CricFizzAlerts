package com.cricFizzAlerts.bean.alert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("alerts")
@NoArgsConstructor
@Data
@ToString
public class AlertDetails {

    @Id
    @PrimaryKeyColumn(name = "alertId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @Column("alertId")
    private String alertId;

    @CassandraType(type = CassandraType.Name.TEXT)
    @Column("mailId")
    private String mailId;

    @CassandraType(type = CassandraType.Name.TEXT)
    @Column("matchType")
    private String matchType;

    @CassandraType(type = CassandraType.Name.INT)
    @Column("seriesId")
    private Long seriesId;

    @CassandraType(type = CassandraType.Name.INT)
    @Column("matchId")
    private Long matchId;

    @CassandraType(type = CassandraType.Name.TEXT)
    @Column("alertType")
    private String alertType;

    @CassandraType(type = CassandraType.Name.INT)
    @Column("timePeriod")
    private Integer timePeriod;

    @CassandraType(type = CassandraType.Name.BOOLEAN)
    @Column("isActive")
    private Boolean isActive;

    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    @Column("alertReceivedDT")
    private LocalDateTime alertReceivedDT;

    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    @Column("lastSentAlertDT")
    @JsonIgnore
    private LocalDateTime lastSentAlertDT;
}
