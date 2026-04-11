package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.InvalidDisbursementException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.DisbursementRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.entity.Disbursement;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.model.enums.DisbursementStatus;
import group3.project.charityweb.repository.DisbursementRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final ProjectRepository projectRepository;

    private DisbursementResponse mapToResponse(Disbursement entity) {
        return DisbursementResponse.builder()
                .disbursementId(entity.getDisbursementId())
                .projectId(entity.getProject().getProjectId())
                .amount(entity.getAmount())
                .disbursementTime(entity.getDisbursementTime())
                .reason(entity.getReason())
                .evidenceURL(entity.getEvidenceURL())
                .recipientInfo(entity.getRecipientInfo())
                .status(entity.getStatus())
                .build();
    }

    @Override
    @Transactional
    public String createDisbursement(String username, String projectId, DisbursementRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));

        boolean isOwner = project.getOrganizations().stream()
                .anyMatch(org -> org.getUsername().equals(username));
        if (!isOwner) {
            throw new UnauthorizedAccessException("Bạn không có quyền giải ngân cho dự án này!");
        }

        BigDecimal totalReceived = project.getCurrentAmount();
        BigDecimal totalDisbursed = disbursementRepository.sumDisbursedAmountByProjectId(projectId, DisbursementStatus.APPROVED);
        BigDecimal availableBalance = totalReceived.subtract(totalDisbursed);

        if (request.getAmount().compareTo(availableBalance) > 0) {
            throw new InvalidDisbursementException(
                    "Số tiền giải ngân (" + request.getAmount() +
                            ") vượt quá số dư khả dụng của dự án (" + availableBalance + ")"
            );
        }

        Disbursement disbursement = new Disbursement();
        disbursement.setProject(project);
        disbursement.setAmount(request.getAmount());
        disbursement.setDisbursementTime(LocalDateTime.now());
        disbursement.setReason(request.getReason());
        disbursement.setEvidenceURL(request.getEvidenceURL());
        disbursement.setRecipientInfo(request.getRecipientInfo());

        disbursement.setStatus(DisbursementStatus.PENDING);

        disbursementRepository.save(disbursement);

        return disbursement.getDisbursementId();
    }

    @Override
    public List<DisbursementResponse> getProjectDisbursements(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Dự án không tồn tại!");
        }

        List<Disbursement> disbursements = disbursementRepository.findByProject_ProjectIdOrderByDisbursementTimeDesc(projectId);
        return disbursements.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DisbursementResponse getDisbursementById(String disbursementId) {
        Disbursement disbursement = disbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin giải ngân!"));
        return mapToResponse(disbursement);
    }
}