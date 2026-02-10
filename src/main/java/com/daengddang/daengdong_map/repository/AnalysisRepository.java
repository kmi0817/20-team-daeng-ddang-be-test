package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.analysis.Analysis;
import com.daengddang.daengdong_map.repository.projection.DailyRecordView;
import com.daengddang.daengdong_map.repository.projection.DateCountView;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {


}
