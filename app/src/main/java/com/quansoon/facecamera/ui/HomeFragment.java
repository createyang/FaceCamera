package com.quansoon.facecamera.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.quansoon.facecamera.R;
import com.quansoon.facecamera.adapter.HomeRecyAdapter;
import com.quansoon.facecamera.base.BaseFragment;
import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.model.ConnectDeviceBean;
import com.quansoon.facecamera.model.PersonModel;
import com.quansoon.facecamera.network.NetChangeObserver;
import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.presenter.HomePresenter;
import com.quansoon.facecamera.presenter.impl.HomePresenterImpl;
import com.quansoon.facecamera.utils.AnimationUtil;
import com.quansoon.facecamera.utils.DatabaseManager;
import com.quansoon.facecamera.utils.DisplayUtil;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.MediaPlayerUtil;
import com.quansoon.facecamera.utils.SharedPreferencesUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.utils.TtsUtil;
import com.quansoon.facecamera.utils.UiUtil;
import com.quansoon.facecamera.view.HomeView;
import com.quansoon.facecamera.widget.CustomViewEmployeeDetails;
import com.quansoon.facecamera.widget.CustomViewTimeDate;
import com.quansoon.facecamera.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Caoy
 */
public class HomeFragment extends BaseFragment implements HomeView, MediaPlayer.OnPreparedListener {

    private SurfaceView mSurfaceView;
    private RecyclerView mRecyclerView;
    private HomePresenter homePresenter;
    public HomeRecyAdapter recyAdapter;
    private ImageView ivEmployeeScanPhoto;
    private RequestOptions options;
    private CustomViewEmployeeDetails customViewEmployeeDetailsFirst;
    private CustomViewEmployeeDetails customViewEmployeeDetailsSecond;
    private CustomViewEmployeeDetails customViewEmployeeDetailsThird;
    private CustomViewEmployeeDetails.ViewHolder detailsFirstViewHolder;
    private CustomViewEmployeeDetails.ViewHolder detailsSecondViewHolder;
    private CustomViewEmployeeDetails.ViewHolder detailsThirdViewHolder;
    private int measuredWidth;

    private ConnectDeviceBean connectDeviceBean;
    private TextView tvSimilarity;
    private LinearLayout laySimilarity;
    private CustomViewTimeDate customViewTimeDate;

    private ArrayList<PersonModel> personList;

    private static final float Scale = .8f;
    private static final float Alpha = .7f;
    private static final float Rotation = 30f;
    private static final int HideTime = 10000;
    private TextView tvEmptyIcon;
    private TextView tvTitle;
    private Bitmap scanImgBitmap;
    private String personName;
    private MediaPlayerUtil mediaPlayerUtil;
    private TtsUtil ttsUtil;
    private DatabaseManager databaseManager;
    private List<PersonModel> modelArrayList;
    private SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    @Override
    public void startScan() {

    }

    @Override
    public void errorScan() {

    }


    /**
     * 匹配员工信息错误
     */
    @Override
    public void errorMatched() {
    }


    private void animationSet(View translationView1, View appearView2, View disappearView3) {
        LogUtils.d("animationSet :"
                + "translationView1:" + translationView1
                + "appearView2:" + appearView2
                + "disappearView3:" + disappearView3
                + "measuredWidth:" + measuredWidth
        );
        /**
         * 1.平移旋转缩放动画（平移）
         */
//        AnimationUtil.startZoomAnim(translationView1, (float) 1, (float) 0.8, 0, measuredWidth+100, 0, 0, null);
        AnimationUtil.starTranslationAndRotateAnim(translationView1
                , (float) 1, (float) Scale
                , 0, measuredWidth
                , 0, 0
                , 0, Rotation
                , 1, Alpha
                , null);
//        AnimationUtil.startAlphaAnima(translationView1,1,0.8f);


        /**
         * 2.出现动画
         */
        AnimationUtil.startScaleToBigAnimation(appearView2, 1, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.pixiedust);
                mediaPlayerUtil.playMusic(assetFileDescriptor, HomeFragment.this);
                AnimationUtil.startScaleToBigAnimation(laySimilarity, 1, null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //1.动画结束后开始计时，超过15显示闲时界面（界面隐藏）
                Handler handler = UiUtil.getHandler();
                handler.removeCallbacks(leisureRun);
                handler.postDelayed(leisureRun, HideTime);
            }
        });
        /**
         * 3消失动画
         */
//        AnimationUtil.startZoomAnim(disappearView3, (float) 0.5, (float) 0, measuredWidth, 0, 0, 0, null);
        AnimationUtil.starTranslationAndRotateAnim(disappearView3
                , (float) Scale, (float) 0
                , measuredWidth, 0
                , 0, 0
                , Rotation, 0
                , Alpha, 1
                , null);


    }

    /**
     * 显示任务
     */
    Runnable leisureRun = new Runnable() {
        @Override
        public void run() {
            ToastUtils.shortShowStr(getContext(), "进入空闲模式");

            //1.隐藏界面
            AnimationUtil.startScaleToBigAnimation(ivEmployeeScanPhoto, 0, null);
            AnimationUtil.startScaleToBigAnimation(laySimilarity, 0, null);

            //2.动画恢复
            restoreAnima();

            //3.集合偏移
            personList.clear();

            //3.将数据移至右侧
//            int size = personList.size();
//            PersonModel personModel;
//            if (personList.size() == 1) {
//                personModel = personList.get(0);
//                recyAdapter.add(personModel, 0);
//            } else if (personList.size() > 1) {
//                personModel = personList.get(personList.size() - 1);
//                recyAdapter.add(personModel, 0);
//                personModel = personList.get(personList.size() - 2);
//                recyAdapter.add(personModel, 0);
//            }

            //4.通知数据清空
//            homePresenter.removeDataList();
//            personList.clear();
        }
    };

    private void restoreAnima() {
        View translationView1 = customViewEmployeeDetailsSecond;
        View appearView2 = customViewEmployeeDetailsThird;
        View disappearView3 = customViewEmployeeDetailsFirst;
        int u = personList.size() % 3;

        if (personList.size() > 1) {
            if (u == 1) {
                //a
                disappearView3 = customViewEmployeeDetailsThird;
                translationView1 = customViewEmployeeDetailsFirst;

            } else if (u == 2) {
                //b
                disappearView3 = customViewEmployeeDetailsFirst;
                translationView1 = customViewEmployeeDetailsSecond;

            } else if (u == 0) {
                //c
                disappearView3 = customViewEmployeeDetailsSecond;
                translationView1 = customViewEmployeeDetailsThird;
            }
        }

        /**
         * 本应该平移的缩小消失
         */
        AnimationUtil.startScaleToBigAnimation(translationView1, 0, null);

        /**
         * 本应该消失的回复到原位
         */
        AnimationUtil.starTranslationAndRotateAnim(disappearView3
                , (float) Scale, (float) 0
                , measuredWidth, 0
                , 0, 0
                , -Rotation, 0
                , Alpha, 1
                , null);
    }

    /**
     * 成功扫描员工
     *
     * @param personList
     */
    @Override
    public void successScanList(final ArrayList<PersonModel> personList) {
        this.personList = personList;
        //获得最新人员的数据
        PersonModel newPersonModel = personList.get(personList.size() - 1);
        //1.设置扫描头像.
//        scanImgBitmap = null;
//        scanImgBitmap = UiUtil.Bytes2Bimap(newPersonModel.getScanImg());
        personName = newPersonModel.getName();

        mediaPlayerUtil.stopMusic();
        ttsUtil.stop();

        Glide.with(this)
                .load(newPersonModel.getScanImg())
                .apply(options)
                .into(ivEmployeeScanPhoto);

        //2.显示相似度
        AnimationUtil.startScaleToBigAnimation(laySimilarity, 0, null);

        if (!StringUtils.isEmpty(newPersonModel.getMatchScore())) {
            tvSimilarity.setText(newPersonModel.getMatchScore() + "%");
        } else {
            tvSimilarity.setText(getString(R.string.str_));
        }

        //3.显示扫描头像
        AnimationUtil.startScaleToBigAnimation(ivEmployeeScanPhoto, 1, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dataChangeUi(personList, true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

    }

    /**
     * 网络请求得到数据通知ui刷新
     *
     * @param personList
     */
    @Override
    public void notifyUiRefreshData(ArrayList<PersonModel> personList) {
        dataChangeUi(personList, false);
    }

    /**
     * 数据改变到ui界面
     *
     * @param personList 数据集
     * @param isAdd      ture:添加插入，false:更新刷新
     */
    private void dataChangeUi(ArrayList<PersonModel> personList, boolean isAdd) {
         /*动画（平移、出现、消失）*/
        View translationView1 = null;
        View appearView2 = customViewEmployeeDetailsFirst;
        View disappearView3 = null;

        int u = personList.size() % 3;
        if (personList.size() >= 3) {
            if (u == 0) {
                translationView1 = customViewEmployeeDetailsSecond;
                appearView2 = customViewEmployeeDetailsThird;
                disappearView3 = customViewEmployeeDetailsFirst;
            } else if (u == 1) {
                translationView1 = customViewEmployeeDetailsThird;
                appearView2 = customViewEmployeeDetailsFirst;
                disappearView3 = customViewEmployeeDetailsSecond;
            } else if (u == 2) {
                translationView1 = customViewEmployeeDetailsFirst;
                appearView2 = customViewEmployeeDetailsSecond;
                disappearView3 = customViewEmployeeDetailsThird;
            }
        } else {
            if (u == 1) {
                translationView1 = null;
                appearView2 = customViewEmployeeDetailsFirst;
                disappearView3 = null;
            } else if (u == 2) {
                translationView1 = customViewEmployeeDetailsFirst;
                appearView2 = customViewEmployeeDetailsSecond;
                disappearView3 = null;
            }
        }

        if (isAdd) {
            animationSet(translationView1, appearView2, disappearView3);
        }

        dataBindUi(translationView1, appearView2, disappearView3, personList, isAdd);
    }

    /**
     * 数据绑定ui界面
     *
     * @param translationView1
     * @param appearView2
     * @param disappearView3
     * @param personModelArrayList
     * @param isAdd
     */
    private void dataBindUi(View translationView1, View appearView2, View disappearView3, ArrayList<PersonModel> personModelArrayList, boolean isAdd) {
        if (appearView2 != null) {
            if (appearView2 == customViewEmployeeDetailsFirst) {
                setViewHolderData(customViewEmployeeDetailsFirst.getViewHolder(), personModelArrayList.get(personModelArrayList.size() - 1));
            }
            if (appearView2 == customViewEmployeeDetailsSecond) {
                setViewHolderData(customViewEmployeeDetailsSecond.getViewHolder(), personModelArrayList.get(personModelArrayList.size() - 1));
            }
            if (appearView2 == customViewEmployeeDetailsThird) {
                setViewHolderData(customViewEmployeeDetailsThird.getViewHolder(), personModelArrayList.get(personModelArrayList.size() - 1));
            }
        }

        PersonModel personModel = personModelArrayList.get(personModelArrayList.size() - 1);
        if (isAdd) {
            tvEmptyIcon.setVisibility(View.GONE);
            recyAdapter.add(personModel, 0);
            databaseManager.saveContact(personModel);
        } else {
            recyAdapter.notifyDataSetChanged();
            databaseManager.saveContact(personModel);
        }
    }



    /**
     * 设置主界面ui
     *
     * @param detailsViewHolder
     * @param newPersonModel
     */
    public void setViewHolderData(CustomViewEmployeeDetails.ViewHolder detailsViewHolder, PersonModel newPersonModel) {
        //name
        if (!StringUtils.isEmpty(newPersonModel.getName())) {
            detailsViewHolder.getmTvName().setText(newPersonModel.getName());
        }
        //time
        if (!StringUtils.isEmpty(newPersonModel.getIoTimeStr())) {
            detailsViewHolder.getmTvTime().setText(newPersonModel.getIoTimeStr());
        }
        //team
        if (!StringUtils.isEmpty(newPersonModel.getGroupName())) {
            detailsViewHolder.getmTvTeam().setText(String.format(getString(R.string.str_team), newPersonModel.getGroupName()));
        } else {
            detailsViewHolder.getmTvTeam().setText(String.format(getString(R.string.str_team), getString(R.string.str_)));
        }
        //type
        if (!StringUtils.isEmpty(newPersonModel.getJob())) {
            detailsViewHolder.getmTvWorkType().setText(String.format(getString(R.string.str_type), newPersonModel.getJob()));
        } else {
            detailsViewHolder.getmTvWorkType().setText(String.format(getString(R.string.str_type), getString(R.string.str_)));
        }

        if (!StringUtils.isEmpty(newPersonModel.getVerifyFacImgUrl())) {
            Glide.with(this)
                    .load(newPersonModel.getVerifyFacImgUrl())
                    .apply(options)
                    .into(detailsViewHolder.getmIvFaceIcon());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            homePresenter.startVideoPlay();
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        mSurfaceView = view.findViewById(R.id.sueface_view);
        mRecyclerView = view.findViewById(R.id.recy_view);
        ivEmployeeScanPhoto = (ImageView) view.findViewById(R.id.iv_employee_scan_photo);
        customViewTimeDate = (CustomViewTimeDate) view.findViewById(R.id.cv_time_date);
        customViewEmployeeDetailsFirst = (CustomViewEmployeeDetails) view.findViewById(R.id.cv_employee_details_1);
        customViewEmployeeDetailsSecond = (CustomViewEmployeeDetails) view.findViewById(R.id.cv_employee_details_2);
        customViewEmployeeDetailsThird = (CustomViewEmployeeDetails) view.findViewById(R.id.cv_employee_details_3);

        laySimilarity = view.findViewById(R.id.lay_similarity);
        tvSimilarity = (TextView) view.findViewById(R.id.tv_similarity);
        tvEmptyIcon = (TextView) view.findViewById(R.id.tv_empty_icon);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        customViewEmployeeDetailsFirst.measure(0, 0);
        int right = (int) DisplayUtil.dip2px(120, getContext());
        measuredWidth = customViewEmployeeDetailsFirst.getMeasuredWidth() + right;

        detailsFirstViewHolder = customViewEmployeeDetailsFirst.getViewHolder();
        detailsSecondViewHolder = customViewEmployeeDetailsSecond.getViewHolder();
        detailsThirdViewHolder = customViewEmployeeDetailsThird.getViewHolder();

        AnimationUtil.startScaleToBigAnimation(ivEmployeeScanPhoto, 0, null);
        AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsFirst, 0, null);
        AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsSecond, 0, null);
        AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsThird, 0, null);
        AnimationUtil.startScaleToBigAnimation(laySimilarity, 0, null);
//        Typeface typeFace = Typeface.createFromAsset(BaseApplication.getContext().getAssets(), "mono.ttf");
        options = new RequestOptions()
                .placeholder(R.mipmap.img_user_b)
                .error(R.mipmap.img_user_b)
//                .skipMemoryCache(true) //跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop();

        Glide.with(this)
                .load(ContextCompat.getDrawable(getContext(), R.mipmap.img_user_b))
                .apply(options)
                .into(ivEmployeeScanPhoto);

        return view;
    }

    @Override
    public void initData() {
        //获得界面传递的数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            connectDeviceBean = (ConnectDeviceBean) bundle.getSerializable(MainActivity.KEY_BUNDLE_CONNECT_DEVICE);
        }
        //缓存的sp数据
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance();
        sharedPreferencesUtils.init(getContext(),connectDeviceBean.getIp());

        NetStateReceiver.registerObserver(new NetChangeObserver() {
            @Override
            public void onNetConnected(int type) {
                customViewTimeDate.requestUpdateDate();
            }

            @Override
            public void onNetDisConnect() {
                ToastUtils.shortShowStr(getContext(), getString(R.string.network_available));
            }
        });

        //初始化声音
        mediaPlayerUtil = MediaPlayerUtil.getInstance(getContext());
        ttsUtil = TtsUtil.init(getContext());

        databaseManager = DatabaseManager.getInstance(mActivity.getBaseContext(),connectDeviceBean.getIp());
        //判断时间是否过了十二点
        String outTimeStr = sharedPreferencesUtils.getValue(Constants.SP.KEY_OUT_TIME, "");
        if (!customViewTimeDate.getCurData().equals(outTimeStr)) {
            //删除昨天数据
            databaseManager.clearEmployees();
        }
        //得到缓存数据
        modelArrayList = databaseManager.getEmployeeList();
        if (modelArrayList == null || modelArrayList.isEmpty()) {
            tvEmptyIcon.setVisibility(View.VISIBLE);
        } else {
            tvEmptyIcon.setVisibility(View.GONE);
        }



        //创建主界面的提供者p，处理业务逻辑
        homePresenter = new HomePresenterImpl(this, connectDeviceBean, mActivity);
        if (!StringUtils.isEmpty(connectDeviceBean.getTitle())) {
            tvTitle.setText(connectDeviceBean.getTitle());
        } else {
            tvTitle.setText(getString(R.string.str_main_title));
        }

        if (recyAdapter == null) {
            recyAdapter = new HomeRecyAdapter(modelArrayList, options);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.scrollToPosition(0);
            manager.setStackFromEnd(false);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            float left = DisplayUtil.dip2px(25, getContext());
            float bot = DisplayUtil.dip2px(30, getContext());
            mRecyclerView.addItemDecoration(new SpaceItemDecoration(left, bot));
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(recyAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customViewTimeDate.removeCall();
        homePresenter.removeRun();
        ttsUtil.release();
        mediaPlayerUtil.releaseMediaPlayer();
        databaseManager.closeDataBase();
        sharedPreferencesUtils.setValue(Constants.SP.KEY_OUT_TIME,customViewTimeDate.getCurData());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        ttsUtil.notifyNewMessage(personName);
    }

}
