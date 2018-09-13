package com.quansoon.facecamera.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import com.bumptech.glide.request.RequestOptions;
import com.quansoon.facecamera.R;
import com.quansoon.facecamera.adapter.HomeRecyAdapter;
import com.quansoon.facecamera.base.BaseFragment;
import com.quansoon.facecamera.model.ConnectDeviceBean;
import com.quansoon.facecamera.model.PersonModel;
import com.quansoon.facecamera.network.NetChangeObserver;
import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.presenter.HomePresenter;
import com.quansoon.facecamera.presenter.impl.HomePresenterImpl;
import com.quansoon.facecamera.utils.AnimationUtil;
import com.quansoon.facecamera.utils.DisplayUtil;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.utils.UiUtil;
import com.quansoon.facecamera.view.HomeView;
import com.quansoon.facecamera.widget.CustomViewEmployeeDetails;
import com.quansoon.facecamera.widget.CustomViewTimeDate;
import com.quansoon.facecamera.widget.SpaceItemDecoration;

import java.util.ArrayList;


/**
 * @author Caoy
 */
public class HomeFragment extends BaseFragment implements HomeView {

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

    private boolean idleState = true;
    private ArrayList<PersonModel> personList;

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
         * 1.平移旋转缩放动画
         */
//        AnimationUtil.startZoomAnim(translationView1, (float) 1, (float) 0.8, 0, measuredWidth+100, 0, 0, null);
        AnimationUtil.starTranslationAndRotateAnim(translationView1, (float) 1, (float) 0.9
                , 0, measuredWidth
                , 0, 0
                , 0, 30f
                , 1, 0.7f
                , null);
//        AnimationUtil.startAlphaAnima(translationView1,1,0.8f);


        /**
         * 2.出现动画
         */
        AnimationUtil.startScaleToBigAnimation(appearView2, 1, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                AnimationUtil.startScaleToBigAnimation(laySimilarity, 1, null);

                //1.动画结束后开始计时，超过一分钟显示闲时界面（界面隐藏）
//                Handler handler = UiUtil.getHandler();
//                handler.removeCallbacks(leisureRun);
//                handler.postDelayed(leisureRun, 100000);
            }
        });
        /**
         * 3消失动画
         */
//        AnimationUtil.startZoomAnim(disappearView3, (float) 0.5, (float) 0, measuredWidth, 0, 0, 0, null);
        AnimationUtil.starTranslationAndRotateAnim(disappearView3
                , (float) 0.9, (float) 0
                , measuredWidth, 0
                , 0, 0
                , -30f, 0
                , 0.7f, 1
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
            AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsFirst, 0, null);
            AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsSecond, 0, null);
            AnimationUtil.startScaleToBigAnimation(customViewEmployeeDetailsThird, 0, null);
            AnimationUtil.startScaleToBigAnimation(laySimilarity, 0, null);
            //2.将数据移至右侧
//            recyAdapter.add(personModel, 0);

//            int size = personList.size();
//            int itemCount = recyAdapter.getItemCount();
//            int cur = size - itemCount;
//            for (int i = 0; i < cur; i++) {
//                PersonModel personModel = personList.get(personList.size() - 1 - i);
//                recyAdapter.add(personModel, 0);
//            }
        }
    };

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
        //1.显示扫描头像
        Bitmap scanImgBitmap = UiUtil.Bytes2Bimap(newPersonModel.getScanImg());
        Glide.with(this)
                .load(scanImgBitmap)
                .apply(options)
                .into(ivEmployeeScanPhoto);

        //2.显示相似度
        if (!StringUtils.isEmpty(newPersonModel.getMatchScore())) {
            tvSimilarity.setText(newPersonModel.getMatchScore() + "%");
        } else {
            tvSimilarity.setText(getString(R.string.str_));
        }

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
         /*动画*/
        View translationView1 = customViewEmployeeDetailsSecond;
        View appearView2 = customViewEmployeeDetailsThird;
        View disappearView3 = customViewEmployeeDetailsFirst;
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
        if (disappearView3 != null) {
            if (isAdd) {
                PersonModel personModel = personModelArrayList.get(personModelArrayList.size() - 3);
                recyAdapter.add(personModel, 0);
            } else {
                recyAdapter.notifyDataSetChanged();
            }
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
                .circleCrop();

        Glide.with(this)
                .load(ContextCompat.getDrawable(getContext(), R.mipmap.img_user_b))
                .apply(options)
                .into(ivEmployeeScanPhoto);

        return view;
    }

    @Override
    public void initData() {
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


        Bundle bundle = getArguments();
        if (bundle != null) {
            connectDeviceBean = (ConnectDeviceBean) bundle.getSerializable(MainActivity.KEY_BUNDLE_CONNECT_DEVICE);
        }
        //创建主界面的提供者p，处理业务逻辑
        homePresenter = new HomePresenterImpl(this, connectDeviceBean, mActivity);

        if (recyAdapter == null) {
            ArrayList<PersonModel> modelArrayList = new ArrayList<>();
            recyAdapter = new HomeRecyAdapter(modelArrayList, options);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
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
        homePresenter.removeRun();
    }

}
