package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.InvalidDisbursementException;
import group3.project.charityweb.exception.ResourceNotFoundException;
import group3.project.charityweb.exception.UnauthorizedAccessException;
import group3.project.charityweb.model.dto.request.DisbursementRequest;
import group3.project.charityweb.model.dto.response.DisbursementResponse;
import group3.project.charityweb.model.entity.Disbursement;
import group3.project.charityweb.model.entity.Project;
import group3.project.charityweb.repository.DisbursementRepository;
import group3.project.charityweb.repository.ProjectRepository;
import group3.project.charityweb.service.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final ProjectRepository projectRepository;

    // --- Mapper ---
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
    public void createDisbursement(String username, String projectId, DisbursementRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án!"));

        // 1. Kiểm tra quyền sở hữu dự án
        boolean isOwner = project.getOrganizations().stream()
                .anyMatch(org -> org.getUsername().equals(username));
        if (!isOwner) {
            throw new UnauthorizedAccessException("Bạn không có quyền giải ngân cho dự án này!");
        }

        // 2. Logic tính toán đối soát tài chính
        BigDecimal totalReceived = project.getCurrentAmount(); // Tổng tiền đã quyên góp được
        BigDecimal totalDisbursed = disbursementRepository.sumDisbursedAmountByProjectId(projectId); // Tổng tiền đã chi
        BigDecimal availableBalance = totalReceived.subtract(totalDisbursed); // Số dư khả dụng

        if (request.getAmount().compareTo(availableBalance) > 0) {
            throw new InvalidDisbursementException(
                    "Số tiền giải ngân (" + request.getAmount() +
                            ") vượt quá số dư khả dụng của dự án (" + availableBalance + ")"
            );
        }

        // 3. Tạo bản ghi giải ngân
        Disbursement disbursement = new Disbursement();
        disbursement.setDisbursementId(UUID.randomUUID().toString());
        disbursement.setProject(project);
        disbursement.setAmount(request.getAmount());
        disbursement.setDisbursementTime(LocalDateTime.now());
        disbursement.setReason(request.getReason());
        disbursement.setEvidenceURL(request.getEvidenceURL());
        disbursement.setRecipientInfo(request.getRecipientInfo());

        // Trạng thái: 1 = Hợp lệ (Sau này bạn có thể set = 2 (Pending) nếu muốn làm hệ thống AI OCR Admin check trước khi hiển thị)
        disbursement.setStatus(1);

        disbursementRepository.save(disbursement);
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