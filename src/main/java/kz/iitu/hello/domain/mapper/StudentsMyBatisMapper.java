package kz.iitu.hello.domain.mapper;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentsMyBatisMapper {

    @Select("""
        <script>
        SELECT id, student_name AS studentName, age, group_name AS groupName, gpa, user_id
        FROM students
        WHERE 1 = 1
          <if test='name != null and name != ""'>
            AND LOWER(student_name) LIKE LOWER(CONCAT('%', #{name}, '%'))
          </if>
          <if test='groupName != null and groupName != ""'>
            AND LOWER(group_name) LIKE LOWER(CONCAT('%', #{groupName}, '%'))
          </if>
          <if test='ageFrom != null'>
            AND age <![CDATA[ >= ]]> #{ageFrom}
          </if>
          <if test='ageTo != null'>
            AND age <![CDATA[ <= ]]> #{ageTo}
          </if>
          <if test='gpaFrom != null'>
            AND gpa <![CDATA[ >= ]]> #{gpaFrom}
          </if>
          <if test='gpaTo != null'>
            AND gpa <![CDATA[ <= ]]> #{gpaTo}
          </if>
        ORDER BY ${sortBy} ${sortDirection}
        LIMIT #{limit}
        OFFSET #{offset}
        </script>
        """)
    @Results(id = "studentResultMap", value = {

            @Result(property = "id", column = "id", id = true),
            @Result(property = "studentName", column = "studentName"),
            @Result(property = "age", column = "age"),
            @Result(property = "groupName", column = "groupName"),
            @Result(property = "gpa", column = "gpa"),

            @Result(property = "user",
                    column = "user_id",
                    one = @One(select = "findUserById")),

            @Result(property = "courses",
                    column = "id",
                    many = @Many(select = "findCoursesByStudentId"))
    })
    List<Student> findStudents(@Param("name") String name,
                               @Param("groupName") String groupName,
                               @Param("ageFrom") Integer ageFrom,
                               @Param("ageTo") Integer ageTo,
                               @Param("gpaFrom") Double gpaFrom,
                               @Param("gpaTo") Double gpaTo,
                               @Param("sortBy") String sortBy,
                               @Param("sortDirection") String sortDirection,
                               @Param("limit") int limit,
                               @Param("offset") long offset);

    @Select("""
        SELECT id,
               user_name AS userName,
               email
        FROM users
        WHERE id = #{id}
        """)
    User findUserById(Long id);

    @Select("""
        SELECT c.id,
               c.course_name AS courseName
        FROM courses c
        JOIN student_courses sc ON sc.course_id = c.id
        WHERE sc.student_id = #{studentId}
        """)
    List<Course> findCoursesByStudentId(Long studentId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM students
            WHERE 1 = 1
              <if test='name != null and name != ""'>
                AND LOWER(student_name) LIKE LOWER(CONCAT('%', #{name}, '%'))
              </if>
              <if test='groupName != null and groupName != ""'>
                AND LOWER(group_name) LIKE LOWER(CONCAT('%', #{groupName}, '%'))
              </if>
              <if test='ageFrom != null'>
                AND age <![CDATA[ >= ]]> #{ageFrom}
              </if>
              <if test='ageTo != null'>
                AND age <![CDATA[ <= ]]> #{ageTo}
              </if>
              <if test='gpaFrom != null'>
                AND gpa <![CDATA[ >= ]]> #{gpaFrom}
              </if>
              <if test='gpaTo != null'>
                AND gpa <![CDATA[ <= ]]> #{gpaTo}
              </if>
            </script>
            """)
    long countStudents(@Param("name") String name,
                       @Param("groupName") String groupName,
                       @Param("ageFrom") Integer ageFrom,
                       @Param("ageTo") Integer ageTo,
                       @Param("gpaFrom") Double gpaFrom,
                       @Param("gpaTo") Double gpaTo);
}
