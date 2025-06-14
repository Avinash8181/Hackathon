package com.tms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
}
