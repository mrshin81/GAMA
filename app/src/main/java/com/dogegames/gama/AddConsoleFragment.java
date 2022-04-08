package com.dogegames.gama;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddConsoleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddConsoleFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String TAG="AddConsoleFragment";

    //변수 설정
    DatePickerDialog datePickerDialog=null;

    ConsoleDBHelper consoleDBHelper;

    //UI 객체 연결 변수
    Spinner consoleNameDropdown;
    EditText buyDateET;
    EditText buyPriceET;
    EditText memoET;
    ImageButton showDatePickerBTN;
    ImageButton saveBTN;
    ImageButton cancelBTN;

    TextView consoleMakerTV;
    TextView consoleDateTV;
    TextView consoleSpecTV;
    ImageView consoleIV;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddConsoleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddConsoleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddConsoleFragment newInstance(String param1, String param2) {
        AddConsoleFragment fragment = new AddConsoleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_console, container, false);
        consoleNameDropdown=view.findViewById(R.id.consoleNameDropdown);
        buyDateET=view.findViewById(R.id.consoleBuyDateEditText);
        buyPriceET=view.findViewById(R.id.consoleBuyPriceEditText);
        memoET=view.findViewById(R.id.consoleMemoEditText);
        showDatePickerBTN=view.findViewById(R.id.showDatePickerButton);
        saveBTN=view.findViewById(R.id.saveButton);
        cancelBTN=view.findViewById(R.id.cancelButton);

        consoleMakerTV=view.findViewById(R.id.consoleDescMakerTextView);
        consoleDateTV=view.findViewById(R.id.consoleDescLaunchDateTextView);
        consoleSpecTV=view.findViewById(R.id.consoleDescSpecTextView);
        consoleIV=view.findViewById(R.id.consoleDescImageView);

        consoleDBHelper=ConsoleDBHelper.getInstance(getContext());//MainActivity.context);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

        showDatePickerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Commons)getActivity().getApplication()).showDatePicker(buyDateET);
            }
        });


        saveBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                long timeStamp=System.currentTimeMillis();
                String consoleFirstLetter;
                consoleFirstLetter=consoleNameDropdown.getSelectedItem().toString().substring(0,1);
                if(consoleFirstLetter.equals("")){
                    consoleFirstLetter="x";
                }
                String id=consoleFirstLetter+timeStamp;
                String price=buyPriceET.getText().toString();
                if(price.equals("")){
                    price="0";
                }

                String memo=memoET.getText().toString();
                if(memo.equals("")){
                    memo="NONE";
                }
                Date date;
                try {
                    date=sdf.parse(buyDateET.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    date=new Date();
                }

                String[] consoleImagePathList=getContext().getResources().getStringArray(R.array.console_list);

                String imagePath=consoleImagePathList[consoleNameDropdown.getSelectedItemPosition()];
                imagePath=imagePath.toLowerCase(Locale.ROOT).replaceAll(" ","");
                consoleDBHelper.insertRecord(ConsoleDBHelper.TABLE_NAME, consoleNameDropdown.getSelectedItem().toString(), imagePath, Integer.valueOf(price), date,memo, consoleDBHelper.getNo(), id);

                ((MainActivity)getActivity()).setFrament(MainActivity.NormalFRAGMENT);
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setFrament(MainActivity.NormalFRAGMENT);
            }
        });

        //Spinner 아이템 셋팅
        String[] consoleItems=getResources().getStringArray(R.array.console_list);
        ((Commons)getActivity().getApplication()).setDropdown(consoleItems,consoleNameDropdown);

        consoleNameDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setConsoleSpec(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    void setConsoleSpec(int selectedConsole){
        String[] consoleMaker=getResources().getStringArray(R.array.console_maker);
        String[] consoleDate=getResources().getStringArray(R.array.console_date);
        String[] consoleSpec=getResources().getStringArray(R.array.console_spec);
        String[] consoleImagePath=getResources().getStringArray(R.array.console_imagepath);

        consoleMakerTV.setText(consoleMaker[selectedConsole]);
        consoleDateTV.setText(consoleDate[selectedConsole]);
        consoleSpecTV.setText(consoleSpec[selectedConsole]);
        consoleIV.setImageResource(MainActivity.getImageId(getContext(),consoleImagePath[selectedConsole]));
    }
}