package wondang.icehs.kr.whdghks913.wondanghighschool.timetable;

import java.util.Calendar;

import wondang.icehs.kr.whdghks913.wondanghighschool.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TimeTableScrollTab extends FragmentActivity {
	public final static String[] DAY = { "������", "ȭ����", "������", "�����", "�ݿ���" };

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	String dbName, tableName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timetablescrolltab);

		Intent mIntent = getIntent();
		dbName = mIntent.getStringExtra("dbName");
		tableName = mIntent.getStringExtra("tableName");

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getApplicationContext(), getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// PagerTitleStrip mStrip = (PagerTitleStrip)
		// findViewById(R.id.pager_title_strip);
		// mStrip.setTextColor(Color.DKGRAY);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mViewPager.getChildCount() > 0) {
			Calendar calendar = Calendar.getInstance();
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek > 1 && dayOfWeek < 7) {
				mViewPager.setCurrentItem(dayOfWeek - 2);
			} else {
				mViewPager.setCurrentItem(0);
			}
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		Context mContext;

		public SectionsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			return new TimeTableShow(mContext, position);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return DAY[position];
		}
	}

}