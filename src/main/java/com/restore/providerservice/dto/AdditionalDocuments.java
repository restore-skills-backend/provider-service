package com.restore.providerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdditionalDocuments {
    private MultipartFile[] additionalPhotos;
//    private MultipartFile profileImage;
    private List<String> deletePhotos;
}
