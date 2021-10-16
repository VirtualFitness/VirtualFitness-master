package com.virtualfitness;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.virtualfitness.DAO.record.DAORecord;
import com.virtualfitness.DAO.record.Record;
import com.virtualfitness.enums.DistanceUnit;
import com.virtualfitness.enums.ProgramRecordStatus;
import com.virtualfitness.enums.RecordType;
import com.virtualfitness.enums.WeightUnit;
import com.virtualfitness.utils.UnitConverter;
import com.virtualfitness.views.WorkoutValuesInputView;

public class RecordEditorDialogbox extends Dialog implements View.OnClickListener {

    private final boolean mShowRestTime;
    private final Activity mActivity;
    private final Record mRecord;
    public Dialog d;
    private WorkoutValuesInputView mWorkoutValuesInput;
    private CheckBox mUpdateProgramCheckbox;
    private boolean mCancelled = false;

    public RecordEditorDialogbox(Activity a, Record record) {
        super(a);
        this.mActivity = a;
        mRecord = record;
        mShowRestTime = false;
    }

    public RecordEditorDialogbox(Activity a, Record record, boolean showRestTime) {
        super(a);
        this.mActivity = a;
        mRecord = record;
        mShowRestTime = showRestTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record_editor);
        this.setCanceledOnTouchOutside(false);

        Button updateButton = findViewById(R.id.btn_update);
        Button failedButton = findViewById(R.id.btn_failed);
        Button cancelButton = findViewById(R.id.btn_cancel);
        mUpdateProgramCheckbox = findViewById(R.id.updateProgramCheckbox);
        LinearLayout buttonsLayout = findViewById(R.id.buttons_layout);
        mWorkoutValuesInput = findViewById(R.id.EditorWorkoutValuesInput);

        mWorkoutValuesInput.setRecord(mRecord);
        mWorkoutValuesInput.setShowRestTime(mShowRestTime);

        if (mRecord.getRecordType() == RecordType.PROGRAM_RECORD_TYPE) {
            updateButton.setText(getContext().getString(R.string.success));
            failedButton.setVisibility(View.VISIBLE);
            failedButton.setText(getContext().getString(R.string.fail));
            buttonsLayout.setWeightSum(60);
            mUpdateProgramCheckbox.setVisibility(View.VISIBLE);
            mUpdateProgramCheckbox.setChecked(false);
        } else {
            updateButton.setText(getContext().getString(R.string.update));
            failedButton.setVisibility(View.GONE);
            mUpdateProgramCheckbox.setVisibility(View.GONE);
            buttonsLayout.setWeightSum(40);
        }

        updateButton.setOnClickListener(this);
        failedButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean betterThanExisting = false;
        if (v.getId() == R.id.btn_cancel) {
            mCancelled = true;
            cancel();
        } else if (v.getId() == R.id.btn_update || v.getId() == R.id.btn_failed ) {
            // update record
            DAORecord daoRecord = new DAORecord(mActivity.getBaseContext());
            Record programTemplate = daoRecord.getRecord(mRecord.getTemplateRecordId());
            switch (mRecord.getExerciseType()) {
                case CARDIO:
                    float distance = mWorkoutValuesInput.getDistanceValue();
                    if (mWorkoutValuesInput.getDistanceUnit() == DistanceUnit.MILES) {
                        distance = UnitConverter.MilesToKm(distance); // Always convert to KG
                    }

                    if (programTemplate != null && mUpdateProgramCheckbox.isChecked()) {
                        programTemplate.setDuration(mWorkoutValuesInput.getDurationValue());
                        programTemplate.setDistance(distance);
                        programTemplate.setDistanceUnit(mWorkoutValuesInput.getDistanceUnit());
                    }

                    mRecord.setDuration(mWorkoutValuesInput.getDurationValue());
                    mRecord.setDistance(distance);
                    mRecord.setDistanceUnit(mWorkoutValuesInput.getDistanceUnit());

                    break;
                case ISOMETRIC:
                    float tmpPoids = mWorkoutValuesInput.getWeightValue();
                    tmpPoids = UnitConverter.weightConverter(tmpPoids, mWorkoutValuesInput.getWeightUnit(), WeightUnit.KG); // Always convert to KG

                    if (programTemplate != null && mUpdateProgramCheckbox.isChecked()) {
                        programTemplate.setSets(mWorkoutValuesInput.getSets());
                        programTemplate.setSeconds(mWorkoutValuesInput.getSeconds());
                        programTemplate.setWeight(tmpPoids);
                        programTemplate.setWeightUnit(mWorkoutValuesInput.getWeightUnit());
                    }

                    mRecord.setSets(mWorkoutValuesInput.getSets());
                    mRecord.setSeconds(mWorkoutValuesInput.getSeconds());
                    mRecord.setWeight(tmpPoids);
                    mRecord.setWeightUnit(mWorkoutValuesInput.getWeightUnit());
                    break;
                case STRENGTH:
                    float tmpWeight = mWorkoutValuesInput.getWeightValue();
                    tmpPoids = UnitConverter.weightConverter(tmpWeight, mWorkoutValuesInput.getWeightUnit(), WeightUnit.KG); // Always convert to KG

                    if (programTemplate != null && mUpdateProgramCheckbox.isChecked()) {
                        programTemplate.setSets(mWorkoutValuesInput.getSets());
                        programTemplate.setReps(mWorkoutValuesInput.getReps());
                        programTemplate.setWeight(tmpPoids);
                        programTemplate.setWeightUnit(mWorkoutValuesInput.getWeightUnit());
                    }

                    mRecord.setSets(mWorkoutValuesInput.getSets());
                    mRecord.setReps(mWorkoutValuesInput.getReps());
                    mRecord.setWeight(tmpPoids);
                    mRecord.setWeightUnit(mWorkoutValuesInput.getWeightUnit());
                    break;
            }
            if (mShowRestTime) {
                if (mWorkoutValuesInput.isRestTimeActivated()) {
                    mRecord.setRestTime(mWorkoutValuesInput.getRestTime());
                } else {
                    mRecord.setRestTime(0);
                }
            }
            if(v.getId() == R.id.btn_update) {
                mRecord.setProgramRecordStatus(ProgramRecordStatus.SUCCESS);
            } else if (v.getId() == R.id.btn_failed) {
                mRecord.setProgramRecordStatus(ProgramRecordStatus.FAILED);
            }

            daoRecord.updateRecord(mRecord);
            if (programTemplate != null && mUpdateProgramCheckbox.isChecked()) {
                daoRecord.updateRecord(programTemplate);
            }
            mCancelled = false;
            dismiss();
        }
    }

    public boolean isCancelled() {
        return mCancelled;
    }

}
