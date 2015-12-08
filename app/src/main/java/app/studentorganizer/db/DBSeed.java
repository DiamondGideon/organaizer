package app.studentorganizer.db;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import app.studentorganizer.com.ColorTag;
import app.studentorganizer.com.SubjectType;
import app.studentorganizer.com.TeacherType;
import app.studentorganizer.entities.Subject;
import app.studentorganizer.entities.Task;
import app.studentorganizer.entities.Teacher;

/**
 * Created by Vitalii on 08-Dec-15.
 */
public class DBSeed {
    public static void seed() {
        seedTeachers();
        seedTasks();
        seedSubjects();
    }

    public static void seedTeachers() {
        Teacher teacher = new Teacher();
        teacher.setName("Oleksandr Galkin");
        teacher.setType(TeacherType.DOCENT);
        teacher.setContacts("email:galkin@unicyb.kiev.ua\nskype:kvak313");
        DBFactory.getFactory().getTeacherDAO().addEntity(teacher);

        teacher = new Teacher();
        teacher.setName("Oleksandr Maksymets");
        teacher.setType(TeacherType.POSTGRADUATE);
        teacher.setContacts("email:maksymets@gmail.com");
        DBFactory.getFactory().getTeacherDAO().addEntity(teacher);
    }

    public static void seedTasks() {
        Task task = new Task();
        task.setName("Programming - Labs");
        task.setDeadline(new LocalDate().plusDays(1));
        task.setPoints(30.0);
        task.setProgress(10);
        task.setTarget(40);
//        task.setSubject(new Subject());
//        task.getSubject().setColorTag(ColorTag.GREEN);
        DBFactory.getFactory().getTaskDAO().addEntity(task);

        task = new Task();
        task.setName("Algebra - Smile :)");
        task.setDeadline(new LocalDate().plusDays(1));
        task.setPoints(30.0);
        task.setProgress(75);
        task.setTarget(100);
//        task.setSubject(new Subject());
//        task.getSubject().setColorTag(ColorTag.BLUE);
        DBFactory.getFactory().getTaskDAO().addEntity(task);
    }

    public static void seedSubjects() {
        Subject subject = new Subject();
        subject.setName("Object-oriented programming");
        subject.setType(SubjectType.EXAM);
        subject.setColorTag(ColorTag.BLUE);
        subject.setTeacherId(0L);

        DBFactory.getFactory().getSubjectDAO().addEntity(subject);

        subject = new Subject();
        subject.setName("Mathematical Analysis");
        subject.setType(SubjectType.CREDIT);
        subject.setColorTag(ColorTag.GREEN);
        subject.setTeacherId(1L);
//        subject.setTasks(DBFactory.getFactory().getTaskDAO().getAllEntities());

        DBFactory.getFactory().getSubjectDAO().addEntity(subject);

        subject = new Subject();
        subject.setName("Probability Theory");
        subject.setType(SubjectType.EXAM);
        subject.setColorTag(ColorTag.ORANGE);
        subject.setTeacherId(2L);

        DBFactory.getFactory().getSubjectDAO().addEntity(subject);
    }
}
