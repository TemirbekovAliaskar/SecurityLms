package java12.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java12.dto.request.InstructorRequest;
import java12.dto.response.HTTPResponse;
import java12.dto.response.InfoInstructorsResponse;
import java12.dto.response.InstructorResponse;
import java12.entity.Company;
import java12.entity.Course;
import java12.entity.Group;
import java12.entity.Instructor;
import java12.repository.CompanyRepository;
import java12.repository.CourseRepository;
import java12.repository.InstructorRepository;
import java12.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final CompanyRepository companyRepository;
    private final CourseRepository courseRepository;

    @Override
    public HTTPResponse save(InstructorRequest instructorRequest) {
        try {
            Instructor instructor = new Instructor();
            instructor.setFirstName(instructorRequest.firstName());
            instructor.setLastName(instructorRequest.lastName());
            instructor.setPhoneNumber(instructorRequest.phoneNumber());
            instructor.setSpecialization(instructorRequest.specialization());
            instructorRepository.save(instructor);
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Successfully saved instructor !")
                    .build();
        }catch (Exception e){
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(e.getMessage())
                    .build();
        }

    }

    @Override @Transactional
    public HTTPResponse asSign(Long companyId, Long instructorId) {
        try {
            Company company = companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company with " + companyId + "not found"));
            Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
            company.addInstructor(instructor);
            instructor.addCompany(company);
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Successfully asSigned !")
                    .build();
        }catch (Exception e){
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(e.getMessage())
                    .build();
        }

    }

    @Override
    @Transactional
    public HTTPResponse update(Long instructorId, InstructorRequest instructorRequest) {
        try {
            Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
            instructor.setFirstName(instructorRequest.firstName());
            instructor.setLastName(instructorRequest.lastName());
            instructor.setPhoneNumber(instructorRequest.phoneNumber());
            instructor.setSpecialization(instructorRequest.specialization());

            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Successfully updated !")
                    .build();
        }catch (Exception e){
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override @Transactional
    public HTTPResponse delete(Long instructorId) {
        try {
            Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
            instructorRepository.deleteByInstructorId(instructorId);
            instructorRepository.delete(instructor);
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Successfully deleted !")
                    .build();
        }catch (Exception e){
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public List<InstructorResponse> findAll(Long companyId) {
        return instructorRepository.findAllId(companyId);
    }

    @Override
    public Integer countStudent(Long inId) {
        return instructorRepository.countOfById(inId);
    }

    @Override @org.springframework.transaction.annotation.Transactional
    public HTTPResponse asSignCourse(Long courseId, Long instructorId) {
        try {
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new NoSuchElementException("Not found" + courseId));
            Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
            course.setInstructor(instructor);
            instructor.addCourse(course);
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Successfully asSigned !")
                    .build();
        }catch (Exception e){
            return HTTPResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public InfoInstructorsResponse infoInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
        InfoInstructorsResponse infoInstructorsResponse = instructorRepository.fullInstructorInfos(instructorId);
        Map<String,Integer> groupsInt = infoInstructorsResponse.getGroupsNameWithStudent();
        List<Course>courses = instructor.getCourses();
        for (Course course : courses) {
            for (Group group : course.getGroups()) {
                int count = 0;
                count += group.getStudents().size();
                groupsInt.put(group.getGroupName(),count);
            }
        }
        return infoInstructorsResponse;
    }

//    @Override
//    public HTTPResponse asSign(CompanyRequest companyRequest, Long instructorId) {
//        instructorRepository.findById(instructorId).orElseThrow(() -> new NoSuchElementException("Not found"));
//
//        return null;
//    }
}
