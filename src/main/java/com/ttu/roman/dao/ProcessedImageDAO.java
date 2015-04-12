package com.ttu.roman.dao;

import com.ttu.roman.model.ProcessedImageResult;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public class ProcessedImageDAO extends AbstractDao<ProcessedImageResult> {
    public ProcessedImageDAO() {
        super(ProcessedImageResult.class);
    }
}
