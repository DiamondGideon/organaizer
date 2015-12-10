package app.studentorganizer.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.studentorganizer.R;
import app.studentorganizer.adapters.TeacherSelectAdapter;
import app.studentorganizer.com.ColorTag;
import app.studentorganizer.com.SubjectType;
import app.studentorganizer.db.DBFactory;
import app.studentorganizer.entities.Subject;
import app.studentorganizer.entities.Teacher;
import app.studentorganizer.util.DateUtils;

public class EditSubjectActivity extends BaseActivity {

    protected TeacherSelector mTeacherSelector;

    protected TextView mStartDateText;
    protected TextView mEndDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> subjectTypes = new ArrayList<>();
        for (SubjectType type : SubjectType.values()) {
            subjectTypes.add(type.toString());
        }
        ((Spinner) findViewById(R.id.subject_type)).setAdapter(
                new ArrayAdapter<>(this, R.layout.spinner_item, subjectTypes)
        );
        List<String> subjectTags = new ArrayList<>();
        for (ColorTag tag : ColorTag.values()) {
            subjectTags.add(tag.toString());
        }
        ((Spinner) findViewById(R.id.subject_color_tag)).setAdapter(
                new ArrayAdapter<>(this, R.layout.spinner_item, subjectTags)
        );

        findViewById(R.id.save_button).setOnClickListener(new SubjectSaver());

        findViewById(R.id.teacher_select_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTeacherSelector == null) {
                    mTeacherSelector = new TeacherSelector
                            (EditSubjectActivity.this,
                                    DBFactory.getFactory().getTeacherDAO().getAllEntities(),
                                    findViewById(R.id.teacher_view));
                }
                mTeacherSelector.showDialog();
            }
        });

        mStartDateText = (TextView) findViewById(R.id.subject_start_date);
        mEndDateText = (TextView) findViewById(R.id.subject_end_date);
        View startDatePicker = findViewById(R.id.subject_start_date_button);
        View endDatePicker = findViewById(R.id.subject_end_date_button);

        initializeDatePicker(mStartDateText, startDatePicker);
        initializeDatePicker(mEndDateText, endDatePicker);
    }

    private void initializeDatePicker(final TextView dateText, View pickerButton) {
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (dateText != null) {
                            dateText.setText(DateUtils.dateToString(year, monthOfYear + 1, dayOfMonth));
                        }
                    }
                };
                new DatePickerDialog(
                        EditSubjectActivity.this,
                        startListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.subject_edit;
    }

    @Override
    public void loadDataFromDB() {
    }

    private class TeacherSelector implements TeacherSelectAdapter.OnTeacherSelectListener {

        private AlertDialog mDialog;
        private Long mTeacherId = -1L;
        private View mTeacherView;

        public TeacherSelector(Activity activity, List<Teacher> teachers, View teacherView) {
            this.mTeacherView = teacherView;
            View view = activity.getLayoutInflater().inflate(
                    R.layout.teacher_select_dialog, null
            );

            mDialog = new AlertDialog.Builder(activity).
                    setCancelable(true).
                    setView(view).
                    create();

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.teacher_list);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(linearLayoutManager);

            TeacherSelectAdapter adapter = new TeacherSelectAdapter(teachers, activity, this);
            recyclerView.setAdapter(adapter);
        }

        public void showDialog() {
            mDialog.show();
        }

        @Override
        public void onTeacherSelected(Long teacherId) {
            mDialog.dismiss();
            this.mTeacherId = teacherId;

            Teacher teacher = DBFactory.getFactory().getTeacherDAO().getByID(teacherId);

            if (mTeacherView != null) {
                ((ImageButton) mTeacherView.findViewById(R.id.teacher_icon)).
                        setImageResource(teacher.getType().getDrawable());
                ((TextView) mTeacherView.findViewById(R.id.teacher_name)).
                        setText(teacher.getName());
                ((TextView) mTeacherView.findViewById(R.id.teacher_type)).
                        setText(teacher.getType().toString());

                // Disable teacher deletion from subject edit activity
                mTeacherView.findViewById(R.id.button_delete).setVisibility(View.GONE);
            }
        }

        public Long getTeacherId() {
            return mTeacherId;
        }
    }

    private class SubjectSaver implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Subject subject = new Subject();
            subject.setName(((TextView) findViewById(R.id.name)).getText().toString());
            subject.setType(SubjectType.valueOf(
                    ((Spinner) findViewById(R.id.subject_type)).
                            getSelectedItem().toString()));
            subject.setColorTag(ColorTag.valueOf(
                    ((Spinner) findViewById(R.id.subject_color_tag)).
                            getSelectedItem().toString()));

            // fixme : debug
            // TODO : validate so that teacher always would be selected
            if (mTeacherSelector != null) {
                subject.setTeacherId(EditSubjectActivity.this.mTeacherSelector.getTeacherId());
            }

            subject.setStartDate(LocalDate.parse(mStartDateText.getText().toString()));
            subject.setEndDate(LocalDate.parse(mEndDateText.getText().toString()));

            DBFactory.getFactory().getSubjectDAO().addEntity(subject);

            finish();
        }
    }
}
