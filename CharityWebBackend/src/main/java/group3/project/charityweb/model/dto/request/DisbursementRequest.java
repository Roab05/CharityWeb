package group3.project.charityweb.model.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DisbursementRequest {
    private BigDecimal amount;
    private String reason;
    private String evidenceURL; // URL ảnh hóa đơn/chứng từ từ Cloudinary/S3
    private String recipientInfo;
}