package com.example.bstuschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleFragment extends Fragment {
    private View view;
    private Context context;
    private HTMLClient htmlClient;
    private String address = "https://www.tu-bryansk.ru/education/schedule/";
    private List<Period> periods;
    private PeriodAdapter periodAdapter;
    private ArrayList<String> groupAdapter;
    private DayScheduleAdapter scheduleAdapter;
    private ScheduleFragmentViewModel vm;
    private boolean hasView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getContext(), "New Schedule Fragment Created", (int)10000).show();
        if (periods == null){
            setPeriods();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.view == null){
            this.view =  inflater.inflate(R.layout.schedule_fragment, container, false);
            //Toast.makeText(getContext(), "New Schedule Fragment View", (int)10000).show();
            setSpinnerBindings();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setGroupsView(@Nullable Period period, @Nullable String selectedGroup) {

        List<String> groups = new ArrayList<String>();

        try {
            String url;
            if (period != null) {
                url = address + "schedule.ajax.php?form=очная&namedata=group" + "&period=" + period.getRequestValue();
            } else {
                url = address + "schedule.ajax.php?form=очная&namedata=group";
            }
            htmlClient.get(url, new PostExecutable() {
                @Override
                public void onPostExecute(String html) {
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Matcher groupM = Pattern.compile("option value=\"(.*?)\"").matcher(html);

                            while (groupM.find()) {
                                groups.add(groupM.group(1));
                            }
                            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Spinner groupsSpinner = (Spinner) view.findViewById(R.id.group);
                                    ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>((Context) context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groups);
                                    groupsSpinner.setAdapter(groupAdapter);
                                    vm.groupAdapter = groupAdapter;
                                    if (selectedGroup == null) {
                                        return;
                                    }
                                    if (!selectedGroup.equals("") && groups.contains(selectedGroup)) {
                                        groupsSpinner.setSelection(groups.indexOf(selectedGroup));
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSchedule(Period period, String payload) {

        List<DaySchedule> days = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    htmlClient.get(address + "schedule.ajax.php?namedata=schedule&period=" + period.getRequestValue() + "&" + payload, new PostExecutable() {
                        @Override
                        public void onPostExecute(String html) {

                            Matcher tr = Pattern.compile("<tr>(.*?)</tr>").matcher(html);
                            Pattern dayNameReg = Pattern.compile("colspan=\"\\d\">(.*?)</td");
                            Pattern timeReg = Pattern.compile("schtime\"(.*?)>.*?([-\\d: ]+)</td");
                            Pattern lesName = Pattern.compile("schname\">(.*?)<");
                            Pattern lesType = Pattern.compile("schtype\">([А-я .,]+)<");
                            Pattern teacherReg = Pattern.compile("schteacher\".*?>([А-я. ]+)");
                            Pattern roomHtmlReg = Pattern.compile("class\">(.*?)</td");
                            Pattern roomIfNotSport = Pattern.compile(">(.*?)<");
                            ArrayList<String> trs = new ArrayList();
                            while (tr.find()) {
                                trs.add(tr.group(1));
                            }
                            ArrayList<Lesson> lessons = new ArrayList<>();
                            for (int i = 0; i < trs.size(); i++) {
                                String currentTr = trs.get(i);
                                Matcher a = dayNameReg.matcher(currentTr);
                                if (a.find()) {
                                    days.add(new DaySchedule(a.group(1)));
                                    lessons = new ArrayList<>();
                                    days.get(days.size() - 1).lessons = lessons;
                                    continue;
                                } else {
                                    Matcher ln = lesName.matcher(currentTr);
                                    Matcher ltm = timeReg.matcher(currentTr);
                                    Matcher ltp = lesType.matcher(currentTr);
                                    Matcher lrm = roomHtmlReg.matcher(currentTr);
                                    Matcher ltch = teacherReg.matcher(currentTr);
                                    Lesson lesson = new Lesson();
                                    if (ln.find()) {
                                        lesson.name = ln.group(1);
                                    }
                                    if (ltm.find()) {
                                        lesson.time = ltm.group(2);
                                    } else {
                                        if (lessons.size() > 0) {
                                            lesson.time = lessons.get(lessons.size() - 1).time;
                                        }
                                    }
                                    if (ltp.find()) {
                                        lesson.type = ltp.group(1);
                                    }
                                    if (lrm.find()) {
                                        if (!lrm.group(1).contains(">")) {
                                            lesson.room = lrm.group(1);
                                        } else {
                                            Matcher r = roomIfNotSport.matcher(lrm.group(1));
                                            if (r.find()) {
                                                lesson.room = r.group(1);
                                            }

                                        }
                                    }
                                    if (ltch.find()) {
                                        lesson.teacher = ltch.group(1);
                                    }
                                    lessons.add(lesson);
                                }

                            }
                            Log.i("", String.valueOf(days));

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView schedule = view.findViewById(R.id.schedule);
                schedule.setLayoutManager(new LinearLayoutManager(context));
                DayScheduleAdapter dsa = new DayScheduleAdapter(context, days);
                vm.scheduleAdapter = dsa;
                schedule.setAdapter(dsa);
            }
        });


    }

    private void setSpinnerBindings(){
        vm = new ViewModelProvider(this).get(ScheduleFragmentViewModel.class);
        if (vm.periodAdapter != null){
            ((Spinner) view.findViewById(R.id.periodsSpinner)).setAdapter(vm.periodAdapter);
        }
        else {
            vm.periodAdapter = new PeriodAdapter(context, R.layout.period_item, periods);
            ((Spinner) view.findViewById(R.id.periodsSpinner)).setAdapter(vm.periodAdapter);
        }



        SharedPreferences sharedPreferences = context.getSharedPreferences("BSTUScheduleData", Context.MODE_PRIVATE);
        String selectedGroupBefore = sharedPreferences.getString("group", "");

        Spinner periodSpinner =view.findViewById(R.id.periodsSpinner);
        Spinner groupSpinner = view.findViewById(R.id.group);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selecterGroup = groupSpinner.getSelectedItem();
                vm.scheduleAdapter = null;
                vm.groupAdapter = null;
                vm.periodAdapter = null;
                if (!selectedGroupBefore.equals("")){
                    setGroupsView((Period)parent.getItemAtPosition(position), selectedGroupBefore);
                }
                else {
                    if (selecterGroup != null){
                        setGroupsView((Period) parent.getItemAtPosition(position), selecterGroup.toString());
                    }
                    else {
                        setGroupsView((Period)parent.getItemAtPosition(position), null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.scheduleAdapter = null;
                vm.groupAdapter = null;
                vm.periodAdapter = null;
                setSchedule((Period) periodSpinner.getSelectedItem(), "group=" + parent.getItemAtPosition(position).toString());
                SharedPreferences sharedPreferences = context.getSharedPreferences("BSTUScheduleData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("group", parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

    }
    private void setPeriods(){
        periods = new ArrayList<>();
        htmlClient = new HTMLClient("GET", 10000, "UTF-8");
        try {
            htmlClient.get(address, new PostExecutable() {
                @Override
                public void onPostExecute(String html) {

                    //Toast.makeText(context, "HTML DOWNLOADED " + html.length(), (int) 10000).show();
                    List<String> select = new ArrayList<String>();
                    Pattern regSelect = Pattern.compile("select .*? id=\"period\" >\\n\\t+(.*?)</select>");
                    Matcher period = regSelect.matcher(html);
                    while (period.find()) {
                        select.add(period.group(1));
                    }
                    //Toast.makeText(context, String.valueOf(select.size()), (int) 10000).show();
                    Pattern regPerionVal = Pattern.compile("value=\"([0-9_-]+)\"");
                    Pattern regPerionShow = Pattern.compile(">([-А-я\\d: ]+)<");

                    Matcher periodVal = regPerionVal.matcher(select.get(0));
                    Matcher periodShow = regPerionShow.matcher(select.get(0));
                    Log.i("periodSearch", select.get(0));
                    while (periodShow.find()) {
                               /* if (!periodShow.group(1).contains(":")){
                                    continue;
                                }*/
                        if (periodVal.find()){
                            periods.add(new Period(periodVal.group(1), periodShow.group(1)));
                        }

                    }
                    //Toast.makeText(context, String.valueOf(periods.size()), (int) 10000).show();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ScheduleFragmentViewModel extends ViewModel {

        public PeriodAdapter periodAdapter;
        public ArrayAdapter<String> groupAdapter;
        public DayScheduleAdapter scheduleAdapter;

    }


}
