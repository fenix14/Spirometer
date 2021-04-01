package com.fenix.spirometer.room;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.LoginState;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.database.AppDatabase;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.room.model.TestReportModel;
import com.fenix.spirometer.room.model.TestReportWithData;
import com.fenix.spirometer.room.model.VoltageData;
import com.fenix.spirometer.room.util.ModelObjTransUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestReportRepository {
    private static volatile TestReportRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;
    private final MutableLiveData<LoginState> mdLoginState = new MutableLiveData<>();
    final MutableLiveData<List<Operator>> mdOperators = new MutableLiveData<>();
    private Context context;

    public TestReportRepository() {
        context = MyApplication.getInstance();
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static TestReportRepository getInstance() {
        if (instance == null) {
            instance = new TestReportRepository();
        }
        return instance;
    }

    public void insertReports(List<TestReportModel> reportModels) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            database.testReportDao().insertTestReports(reportModels);
        });
    }

    public void insert(TestReport report) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            database.memberDao().insert(report.getMember());
            database.operatorDao().insertOperator(report.getOperator());
            database.testReportDao().insert(new TestReportModel(report.getTimeMills(), report.getMember().getId(), report.getOperator().getUserId()));
            database.voltageDataDao().insert(new VoltageData(report.getTimeMills(), report.getData()));
        });
    }

    public MutableLiveData<TestReport> getReport(long timeStamp) {
        final MutableLiveData<TestReport> mdTestReport = new MutableLiveData<>();
        executor.execute(() -> {
             TestReportWithData testReportWithData = database.testReportDao().getReport(timeStamp);
             if (testReportWithData != null) {
                 mdTestReport.postValue(ModelObjTransUtil.model2Object(testReportWithData));
             }
        });
        return mdTestReport;
    }

    public LiveData<List<SimpleReport>> getSimpleReports() {
        return database.testReportDao().getAllSimpleReports();
    }
}
