package java12.dto.request;

import java12.entity.enums.Specialization;

public record InstructorRequest( String firstName,String lastName,String phoneNumber,Specialization specialization) {
}
