package java12.dto.request;

import java12.entity.enums.StudyFormat;

public record StudentRequest(String fullName,String lastName,String phoneNumber,String email,StudyFormat studyFormat) {
}
