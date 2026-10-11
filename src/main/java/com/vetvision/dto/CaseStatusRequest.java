package com.vetvision.dto;

import com.vetvision.entity.CaseStatus;
import jakarta.validation.constraints.NotNull;

public record CaseStatusRequest(@NotNull CaseStatus status) { }