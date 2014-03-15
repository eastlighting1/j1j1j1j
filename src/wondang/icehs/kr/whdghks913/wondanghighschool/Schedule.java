package wondang.icehs.kr.whdghks913.wondanghighschool;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.TextView;

import com.tistory.whdghks913.croutonhelper.CroutonHelper;

import de.keyboardsurfer.android.widget.crouton.Style;

public class Schedule extends Activity {
	private final String loadingList = "�����͸� �������� �ֽ��ϴ�..";
	private final String monthError = "�ùٸ��� �ʽ��ϴ�";
	private final String noData = "�����Ͱ� �������� �ʽ��ϴ�,\n���� ������Ʈ�� �����Ͱ� �߰��˴ϴ�";

	private ScheduleListViewAdapter mAdapter;
	private ListView mListView;
	private Handler mHandler;

	private CroutonHelper mHelper;

	private ProgressDialog mDialog;

	private Calendar mCalendar;
	private SharedPreferences ScheduleList, Info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		mListView = (ListView) findViewById(R.id.mScheduleList);
		mAdapter = new ScheduleListViewAdapter(this);
		mListView.setAdapter(mAdapter);

		mHandler = new MyHandler(this);

		mCalendar = Calendar.getInstance();

		Info = getSharedPreferences("Info", 0);
		ScheduleList = getSharedPreferences("March", 0);

		mHelper = new CroutonHelper(this);
		mHelper.setText("�б� ���� ���� �Դϴ�");
		mHelper.setStyle(Style.INFO);
		mHelper.show();

		sync();
	}

	private void sync() {
		mAdapter.clearData();

		new Thread() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);

				try {
					PackageManager packageManager = Schedule.this
							.getPackageManager();
					PackageInfo infor = packageManager.getPackageInfo(
							getPackageName(), PackageManager.GET_META_DATA);
					final int code = infor.versionCode;

					if (Info.getInt("update_code", 0) != code
							|| ScheduleList.getInt("days", 0) == 0) {
						PreferenceData mData = new PreferenceData();

						mData.copyDB(Schedule.this, getPackageName(),
								"March.xml", true);
						mData.copyDB(Schedule.this, getPackageName(),
								"April.xml", true);
						mData.copyDB(Schedule.this, getPackageName(),
								"May.xml", true);
						mData.copyDB(Schedule.this, getPackageName(),
								"June.xml", true);
						mData.copyDB(Schedule.this, getPackageName(),
								"July.xml", true);
						mData.copyDB(Schedule.this, getPackageName(),
								"August.xml", true);

						Info.edit().putInt("update_code", code).commit();
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				mHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	private String getMonth(int month) {
		switch (month) {
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		}
		return null;
	}

	private String getMonthKorean(int month) {
		switch (month) {
		case 0:
			return "1��";
		case 1:
			return "2��";
		case 2:
			return "3��";
		case 3:
			return "4��";
		case 4:
			return "5��";
		case 5:
			return "6��";
		case 6:
			return "7��";
		case 7:
			return "8��";
		case 8:
			return "9��";
		case 9:
			return "10��";
		case 10:
			return "11��";
		case 11:
			return "12��";
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mDialog != null)
			mDialog.dismiss();

		mHelper.cencle(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.schedule, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int ItemId = item.getItemId();

		if (ItemId == R.id.back) {
			int year = mCalendar.get(Calendar.YEAR);
			int month = mCalendar.get(Calendar.MONTH);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);

			if (--month < 0) {
				mHelper.clearCroutonsForActivity();
				mHelper.setText(monthError);
				mHelper.setStyle(Style.ALERT);
				mHelper.show();
			} else {
				mCalendar.set(year, month, day);
				sync();
			}

		} else if (ItemId == R.id.forward) {
			int year = mCalendar.get(Calendar.YEAR);
			int month = mCalendar.get(Calendar.MONTH);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);

			if (++month > 11) {
				mHelper.clearCroutonsForActivity();
				mHelper.setText(monthError);
				mHelper.setStyle(Style.ALERT);
				mHelper.show();
			} else {
				mCalendar.set(year, month, day);
				sync();
			}

		} else if (ItemId == R.id.sync) {
			sync();
		}

		return super.onOptionsItemSelected(item);
	}

	private class MyHandler extends Handler {
		private final WeakReference<Schedule> mActivity;

		public MyHandler(Schedule Schedule) {
			mActivity = new WeakReference<Schedule>(Schedule);
		}

		@Override
		public void handleMessage(Message msg) {
			Schedule activity = mActivity.get();
			if (activity != null) {

				if (msg.what == 0) {
					if (mDialog == null) {
						mDialog = ProgressDialog.show(Schedule.this, "",
								loadingList);
					}

				} else if (msg.what == 1) {
					ScheduleList = getSharedPreferences(
							getMonth(mCalendar.get(Calendar.MONTH)), 0);

					int days = ScheduleList.getInt("days", 9999);

					if (days != 9999) {
						for (int i = 1; i < days; i++) {
							String Schedule = ScheduleList.getString(
									Integer.toString(i), null);
							if (Schedule != null) {
								String toString = Integer.toString(i);
								String dayOfWeek = ScheduleList.getString(
										toString + "_Day", null);
								if (i < 10)
									toString = "0" + toString;
								mAdapter.addItem(toString + "��", dayOfWeek,
										Schedule);
							}
						}
					} else {
						mHelper.clearCroutonsForActivity();
						mHelper.setText(noData);
						mHelper.setStyle(Style.ALERT);
						mHelper.setAutoTouchCencle(true);
						mHelper.show();
					}

					((TextView) findViewById(R.id.mMonth))
							.setText(getMonthKorean(mCalendar
									.get(Calendar.MONTH)));

					mAdapter.notifyDataSetChanged();
					mDialog.dismiss();
				}
			}
		}
	}
}