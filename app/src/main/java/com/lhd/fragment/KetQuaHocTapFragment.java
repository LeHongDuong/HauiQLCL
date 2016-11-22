package com.lhd.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.baoyz.widget.PullRefreshLayout;
import com.ken.hauiclass.R;
import com.lhd.activity.ListActivity;
import com.lhd.activity.MainActivity;
import com.lhd.database.SQLiteManager;
import com.lhd.item.DiemThiTheoMon;
import com.lhd.item.ItemBangKetQuaHocTap;
import com.lhd.item.KetQuaHocTap;
import com.lhd.item.LichThi;
import com.lhd.item.SinhVien;
import com.lhd.service.MyService;
import com.lhd.task.ParserKetQuaHocTap;
import com.lhd.task.ParserKetQuaThiTheoMon;
import com.lhd.task.ParserLichThiTheoMon;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Double.parseDouble;

/**
 * Created by Faker on 8/17/2016.
 */
public class KetQuaHocTapFragment extends Fragment {
    public static final String KEY_OBJECT = "send_object";
    private int mTheme = R.style.Theme_AlertDialogPro_Holo_Light;
    public static final String KEY_ACTIVITY = "key_start_activity";
    private RecyclerView recyclerView;
    private TextView tVnull;
    private ProgressBar progressBar;
    private LinearLayout toolbar;
    private SQLiteManager sqLiteManager;
    private ArrayList<ItemBangKetQuaHocTap> bangKetQuaHocTaps;
    private  ArrayList<DiemThiTheoMon> diemThiTheoMons;
    private  ArrayList<LichThi> lichThis;
    private SinhVien sv;
    private String maSV;
    private int indexTab;
    private TextView tvTitle,tv1,tv2;
    private PullRefreshLayout pullRefreshLayout;
    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.list_layout,container,false);
        initView(view);
        return view;
    }
    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }catch (Exception e) {
            return false;
        }
    }
    /**
     * khởi tạo các view
     *
     * @param view
     */

    private void initView(View view) {
        sqLiteManager=new SQLiteManager(getContext());
        maSV=getArguments().getString(MainActivity.MA_SV);
        indexTab=getArguments().getInt(MyService.TAB_POSITON);
        pullRefreshLayout= (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        toolbar= (LinearLayout) view.findViewById(R.id.toolbar_list_activity);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setVisibility(View.VISIBLE);
        tvTitle= (TextView) toolbar.findViewById(R.id.tb_title);
        tv1= (TextView) toolbar.findViewById(R.id.tb_text1);
        tv2= (TextView) toolbar.findViewById(R.id.tb_text2);
        progressBar= (ProgressBar) view.findViewById(R.id.pg_loading);
        tVnull= (TextView) view.findViewById(R.id.text_null);
        recyclerView= (RecyclerView) view.findViewById(R.id.recle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        refesh();
               switch (indexTab){
                   case 2:
                       setTitleTab("Lich thi");
                       indexTab=2;
                       checkDatabase();
                       pullRefreshLayout.setRefreshing(false);
                       break;
                   case 1:
                       setTitleTab("Kết quả thi");
                       indexTab=1;
                       checkDatabase();
                       pullRefreshLayout.setRefreshing(false);
                       break;
                   default:
                       setTitleTab("Kết quả học tập");
                       indexTab=0;

                       checkDatabase();
                       pullRefreshLayout.setRefreshing(false);
                       break;
               }

        switch (indexTab){
            case 0:
                setTitleTab("Kết quả học tập");

                checkDatabase();
                break;
            case 1:

                checkDatabase();
                break;
            case 2:

                checkDatabase();
                break;
            case 3:

                checkDatabase();
                break;
        }
    }
    private void refesh() {
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()){
                    switch (indexTab){
                        case 0:
                            sqLiteManager.deleteDMon(maSV);
                            break;
                        case 1:
                            sqLiteManager.deleteDThiMon(maSV);
                            break;
                        case 2:
                            sqLiteManager.deleteDLThi(maSV);
                            break;
                    }
                    startParser();
                }else{
                    pullRefreshLayout.setRefreshing(false);
                }

            }
        });
    }
    private void checkDatabase() {
        showProgress();
        switch (indexTab){
            case 2:
                lichThis=sqLiteManager.getAllLThi(maSV);
                if (!lichThis.isEmpty()){
                    showRecircleView();
                    setRecyclerView();
                }else{
                    loadData();
                }
                break;
            case 1:
                diemThiTheoMons=sqLiteManager.getAllDThiMon(maSV);
                if (!diemThiTheoMons.isEmpty()){
                    showRecircleView();
                    setRecyclerView();
                }else{
                    loadData();
                }
                break;
            case 0:
                bangKetQuaHocTaps=sqLiteManager.getBangKetQuaHocTap(maSV);
                if (!bangKetQuaHocTaps.isEmpty()){
                    showRecircleView();
                    setRecyclerView();
                }else{
                  loadData();
                }
                break;

        }

    }
    private void loadData() {
        if (isOnline()){
            showProgress();
            startParser();
        }else{
            cantLoadData();
        }
    }
    private void setRecyclerView() {
        switch (indexTab){
            case 0:
                 KetQuaHocTapFragment.AdapterDiemHocTapTheoMon adapterDiemHocTapTheoMon=new KetQuaHocTapFragment.AdapterDiemHocTapTheoMon(bangKetQuaHocTaps);
                recyclerView.setAdapter(adapterDiemHocTapTheoMon);
                break;
            case 1:
                KetQuaHocTapFragment.AdapterDiemThiMon adapterDiemThiMon=new KetQuaHocTapFragment.AdapterDiemThiMon(diemThiTheoMons);
                recyclerView.removeAllViews();
                recyclerView.setAdapter(adapterDiemThiMon);
                break;
            case 2:
                KetQuaHocTapFragment.AdapterLichThi  adapterLichThi=new AdapterLichThi(lichThis);
                recyclerView.setAdapter(adapterLichThi);
                break;
        }
    }
    /**
     * diem thi theo mon
     */
    private void startParser() {
        switch (indexTab){
            case 0:
                ParserKetQuaHocTap ketQuaHocTapTheoMon=new ParserKetQuaHocTap(0,handler);
                ketQuaHocTapTheoMon.execute(maSV);
                break;
            case 1:
                ParserKetQuaThiTheoMon parserKetQuaThiTheoMon=new ParserKetQuaThiTheoMon(handler);
                parserKetQuaThiTheoMon.execute(maSV);
                break;
            case 2:
                ParserLichThiTheoMon parserKetQuaHocTap=new ParserLichThiTheoMon(handler);
                parserKetQuaHocTap.execute(maSV);
                break;
        }
    }
    public void setTitleTab(String titleTab) {
        sv=sqLiteManager.getSV(maSV);
        if (!(sv instanceof  SinhVien)){
            tvTitle.setText(sv.getTenSV());
            tv1.setText(sv.getMaSV()+" : "+sv.getLopDL());
            tv2.setText(titleTab);
        }else{
            ParserKetQuaHocTap ketQuaHocTapTheoMon=new ParserKetQuaHocTap(2,handler);
            ketQuaHocTapTheoMon.execute(maSV);
        }
    }
    class ItemDiemThiMon extends RecyclerView.ViewHolder{ // tao mot đói tượng
        TextView tenMon;
        TextView dLan1;
        TextView dTKLan1;
        TextView dLan2;
        TextView dTKLan2;
        TextView dCuoiCung;
        TextView ngay1;
        TextView ngay2;
        TextView ghiChu;
        TextView stt;
        public ItemDiemThiMon(View itemView) {
            super(itemView);
            this.tenMon = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_tenlop);
            this.dLan1 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_l1);
            this.dTKLan1 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_tk1);
            this.dLan2 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_l2);
            this.dTKLan2 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_tk2);
            this.dCuoiCung = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_dc);
            this.ngay1 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_n1);
            this.ngay2 = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_n2);
            this.ghiChu = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_gc);
            this.stt = (TextView) itemView.findViewById(R.id.id_item_diem_thi_lop_stt);
        }
    }
    private class AdapterDiemThiMon extends RecyclerView.Adapter<KetQuaHocTapFragment.ItemDiemThiMon> implements RecyclerView.OnClickListener {
        private  ArrayList<DiemThiTheoMon> data;
        public AdapterDiemThiMon( ArrayList<DiemThiTheoMon> data) {
            this.data = data;
        }
        @Override
        public KetQuaHocTapFragment.ItemDiemThiMon onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_diem_thi_theo_mon, parent, false);
            view.setOnClickListener(this);
            KetQuaHocTapFragment.ItemDiemThiMon holder = new KetQuaHocTapFragment.ItemDiemThiMon(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(KetQuaHocTapFragment.ItemDiemThiMon holder, int position) {
            DiemThiTheoMon diemThiTheoMon=data.get(position);
            holder.tenMon.setText(diemThiTheoMon.getTenMon());
            holder.dLan1.setText(diemThiTheoMon.getdLan1());
            holder.dLan2.setText(diemThiTheoMon.getdLan2());
            holder.dCuoiCung.setText(diemThiTheoMon.getdCuoiCung());
            String tk1=diemThiTheoMon.getdTKLan1().trim();
            String tk2=diemThiTheoMon.getdTKLan2().trim();
            holder.dTKLan2.setText(tk2);
            holder.dTKLan1.setText(tk1);
            holder.ngay1.setText(diemThiTheoMon.getNgay1());
            holder.ngay2.setText(diemThiTheoMon.getNgay2());
            holder.ghiChu.setText(diemThiTheoMon.getGhiChu());
            holder.stt.setText(""+(position+1));
            String dc=diemThiTheoMon.getdCuoiCung().split(" ")[0];
            dc=dc.trim();
            double th = 0;
            double n = 0;
            if (diemThiTheoMon.getNgay1().split("").length>3){
                n=Double.parseDouble(diemThiTheoMon.getNgay1().split("/")[2]);
                th = Double.parseDouble(diemThiTheoMon.getNgay1().split("/")[1]);
            }
            if (dc.equals("(I)")){
                holder.dCuoiCung.setText("*");
                holder.dCuoiCung.setTextColor(Color.parseColor("#42A5F5"));
            }else{
                holder.dCuoiCung.setText(dc);

                if (isDouble(dc)){
                    double d= Double.parseDouble(dc);
                    if (d>=8.5){
                        holder.dCuoiCung.setTextColor(Color.parseColor("#FF0000"));
                        holder.dCuoiCung.setText("A");
                    }else if(d>=7.7&&n>=2015){
                        if (n==2015&&th<=9){
                            holder.dCuoiCung.setText("B");
                            holder.dCuoiCung.setTextColor(Color.parseColor("#FFD600"));
                        }else{
                            holder.dCuoiCung.setText("B+");
                            holder.dCuoiCung.setTextColor(Color.parseColor("#FF8C00"));
                        }
                    }else if(d>=7.0){
                        holder.dCuoiCung.setText("B");
                        holder.dCuoiCung.setTextColor(Color.parseColor("#FFD600"));
                    }else if(d>=6.2&&n>=2015){
                        if (n==2015&&th<=9){
                            holder.dCuoiCung.setTextColor(Color.parseColor("#CCFF90"));
                            holder.dCuoiCung.setText("C");
                        }else{
                            holder.dCuoiCung.setText("C+");
                            holder.dCuoiCung.setTextColor(Color.parseColor("#64DD17"));
                        }
                    }else if(d>=5.5){
                        holder.dCuoiCung.setText("C");
                        holder.dCuoiCung.setTextColor(Color.parseColor("#CCFF90"));
                    }else if(d>=4.7&&n>=2015){

                        if (n==2015&&th<=9){
                            holder.dCuoiCung.setTextColor(Color.parseColor("#84FFFF"));
                            holder.dCuoiCung.setText("D");
                        }else{
                            holder.dCuoiCung.setText("D+");
                            holder.dCuoiCung.setTextColor(Color.parseColor("#00B8D4"));
                        }
                    }else if(d>=4.0){
                        holder.dCuoiCung.setText("D");
                        holder.dCuoiCung.setTextColor(Color.parseColor("#84FFFF"));
                    }else{
                        holder.dCuoiCung.setText("F");
                        holder.dCuoiCung.setTextColor(Color.parseColor("#D500F9"));
                    }
                }
            }

        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            Intent intent=new Intent(getActivity(),ListActivity.class);
            intent.putExtra(KEY_OBJECT, (Serializable) data.get(itemPosition));
            Bundle bundle=new Bundle();
            bundle.putInt(KEY_ACTIVITY,3);
            intent.putExtra("action",bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.left_end, R.anim.right_end);
        }
    }
    public static boolean  isDouble(String str) {
        try {
            parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * lich thi
     */
    class ItemLichThi extends RecyclerView.ViewHolder{ // tao mot đói tượng
        TextView tenMon;
        TextView sbd;
        TextView thuThi;
        TextView phong;
        TextView ngayThi;
        TextView caThi;
        TextView lanThi;
        TextView stt;
        public ItemLichThi(View itemView) {
            super(itemView);
            this.tenMon = (TextView) itemView.findViewById(R.id.id_item_lich_thi_tenlop);
            this.sbd = (TextView) itemView.findViewById(R.id.id_item_lich_thi_sbd);
            this.thuThi = (TextView) itemView.findViewById(R.id.id_item_lich_thi_thu);
            this.phong = (TextView) itemView.findViewById(R.id.id_item_lich_thi_phong);
            this.ngayThi = (TextView) itemView.findViewById(R.id.id_item_lich_thi_ngay);
            this.caThi = (TextView) itemView.findViewById(R.id.id_item_lich_thi_gio);
            this.lanThi = (TextView) itemView.findViewById(R.id.id_item_lich_thi_lan);
            this.stt = (TextView) itemView.findViewById(R.id.id_item_lich_thi_stt);

        }
    }
    private class AdapterLichThi extends RecyclerView.Adapter<KetQuaHocTapFragment.ItemLichThi> implements RecyclerView.OnClickListener {
        private  ArrayList<LichThi> data;
        public AdapterLichThi( ArrayList<LichThi> data) {
            this.data = data;
        }
        @Override
        public KetQuaHocTapFragment.ItemLichThi onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_lich_thi, parent, false);
            view.setOnClickListener(this);
            KetQuaHocTapFragment.ItemLichThi holder = new KetQuaHocTapFragment.ItemLichThi(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(KetQuaHocTapFragment.ItemLichThi holder, int position) {
            LichThi lichThi=data.get(position);
            holder.tenMon.setText(lichThi.getMon()+"");
            holder.sbd.setText(lichThi.getSbd()+"");
            holder.thuThi.setText(lichThi.getThu()+"");
            holder.phong.setText(lichThi.getPhong()+"");
            holder.ngayThi.setText(lichThi.getNgay()+"");
            holder.caThi.setText(lichThi.getGio()+"");
            holder.lanThi.setText(lichThi.getLanthi()+"");
            holder.stt.setText((position+1)+"");
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
        }
    }
    /**
     * hien danh sach
     */
    private void showRecircleView() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        tVnull.setVisibility(View.GONE);
    }
    /**
     * hien Progress cho doi
     */
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tVnull.setVisibility(View.GONE);
    }
    /**
     * -  online thi tiep tuc start lay du lieu tu html
     *
     * - khong online thi show snackbar
     * - onclick se kiem tra lai online
     */

    private void cantLoadData() {
        showTextNull();
        tVnull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()){
                    showProgress();
                    startParser();
                }else {
                    final Snackbar snackbar=Snackbar.make(recyclerView, "Vui lòng bật kết nối internet!",Snackbar.LENGTH_SHORT);

                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.setAction("Bật wifi", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(true);
                            showProgress();
                            snackbar.dismiss();
                            startParser();

                        }
                    });
                    snackbar.show();


                }
            }
        });
    }
    /**
     * hien textview the hien bi loi khi lay data
     */
    private void showTextNull() {
        progressBar.setVisibility(View.GONE);
        tVnull.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
    /**
     * chay mot luong de lay du lieu tu html
     */
    /**
     * lay danh sach diem thanh phan trong database
     * @return 1 danh sach cac ItemBangKetQuaHocTap
     */
    /**
     * tạo một đối tượng trung gian để luu 1 doi tuong ItemBangKetQuaHocTap
     */
    class ItemDanhSachLop extends RecyclerView.ViewHolder{ // tao mot đói tượng
        TextView tvTenLop;
        TextView tvMaLop;
        TextView tvD1;
        TextView tvD2;
        TextView tvD3;
        TextView tvDDK;
        TextView tvSoTietNghi;
        TextView tvDTB;
        TextView tvDieuKien;
        TextView stt;
        public ItemDanhSachLop(View itemView) {
            super(itemView);

            this.tvTenLop = (TextView) itemView.findViewById(R.id.id_item_diem_lop_tenlop);
            this.tvMaLop = (TextView) itemView.findViewById(R.id.id_item_diem_lop_masv);
            this.tvD1 = (TextView) itemView.findViewById(R.id.id_item_diem_lop_d1);
            this.tvD2 = (TextView) itemView.findViewById(R.id.id_item_diem_lop_d2);
            this.tvD3 = (TextView) itemView.findViewById(R.id.id_item_diem_lop_d3);
            this.tvDDK = (TextView) itemView.findViewById(R.id.id_item_diem_lop_d4);
            this.tvSoTietNghi = (TextView) itemView.findViewById(R.id.id_item_diem_lop_so_tiet_nghi);
            this.tvDTB = (TextView) itemView.findViewById(R.id.id_item_diem_lop_dtb);
            this.tvDieuKien = (TextView) itemView.findViewById(R.id.id_item_diem_lop_dieuKien);
            this.stt = (TextView) itemView.findViewById(R.id.id_item_diem_lop_stt);
        }
    }
    class AdapterDiemHocTapTheoMon extends RecyclerView.Adapter<ItemDanhSachLop> implements RecyclerView.OnClickListener {
        private  ArrayList<ItemBangKetQuaHocTap> data;
        public AdapterDiemHocTapTheoMon( ArrayList<ItemBangKetQuaHocTap> data) {
            this.data = data;
        }
        @Override
        public ItemDanhSachLop onCreateViewHolder( ViewGroup parent,  int viewType) {
             View view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_point_class, parent, false);
            view.setOnClickListener(this);
             ItemDanhSachLop holder = new ItemDanhSachLop(view);
            return holder;
        }
        @Override
        public void onBindViewHolder( ItemDanhSachLop holder,  int position) {
            ItemBangKetQuaHocTap item = bangKetQuaHocTaps.get(position);
            holder.tvTenLop.setText(item.getTenMon());
            holder.tvMaLop.setText(item.getMaMon());
            holder.tvD1.setText(item.getD1());
            holder.tvD2.setText(item.getD2());
            holder.tvD3.setText(item.getD3());
            holder.tvDDK.setText(item.getdGiua());
            holder.tvDieuKien.setText(item.getDieuKien());
            holder.tvSoTietNghi.setText(item.getSoTietNghi());
            holder.tvDTB.setText(item.getdTB());
            holder.stt.setText(""+(position+1));
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            ItemBangKetQuaHocTap diemHocTapTheoMon=bangKetQuaHocTaps.get(itemPosition);
            showCustomViewDialog(diemHocTapTheoMon);
        }
    }
    private void showCustomViewDialog(final ItemBangKetQuaHocTap itemBangKetQuaHocTap) {
        String[] list = new String[]{"Bảng điểm học tâp", "Kế hoạch thi theo lớp", "Trò Chuyện"};
        final AlertDialogPro.Builder alertDialogPro=new AlertDialogPro.Builder(getContext(), mTheme);
        alertDialogPro.setTitle(itemBangKetQuaHocTap.getTenMon()).setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogPro.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }
                });
                final Intent intent=new Intent(getActivity(),ListActivity.class);
                intent.putExtra(KEY_OBJECT, (Serializable) itemBangKetQuaHocTap);
                Bundle bundle=new Bundle();
                bundle.putInt(KEY_ACTIVITY,i);
                intent.putExtra("action",bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.left_end, R.anim.right_end);

            }
        }).show();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                switch (msg.arg1){
                    case 0:
                        KetQuaHocTap b;
                        setTitleTab("Kết quả học tập");
                        b= (KetQuaHocTap) msg.obj; // lay tren internet
                        if (b.getSinhVien()!=null&&!b.getBangKetQuaHocTaps().isEmpty()){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            sqLiteManager.insertSV(sv);
                            bangKetQuaHocTaps=b.getBangKetQuaHocTaps();
                             if (sqLiteManager.getBangKetQuaHocTap(maSV).size()<bangKetQuaHocTaps.size()){
                                 for (ItemBangKetQuaHocTap diemHocTapTheoMon:bangKetQuaHocTaps){
                                     sqLiteManager.insertDMon(diemHocTapTheoMon,sv.getMaSV());
                                 }
                              }
                            pullRefreshLayout.setRefreshing(false);
                            showRecircleView();
                            setRecyclerView();
                        }
                        break;
                    case 2:
                        diemThiTheoMons= (ArrayList<DiemThiTheoMon>) msg.obj;
                        if (!diemThiTheoMons.isEmpty()){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            setTitleTab("Kết quả thi");
                            if (sqLiteManager.getAllDThiMon(maSV).size()<diemThiTheoMons.size()){
                                for (DiemThiTheoMon diemHocTapTheoLop:diemThiTheoMons){
                                    sqLiteManager.insertDThiMon(diemHocTapTheoLop,maSV);
                                }
                            }
                            showRecircleView();
                            pullRefreshLayout.setRefreshing(false);
                            setRecyclerView();
                        }
                        break;
                    case 5:
                       lichThis= (ArrayList<LichThi>) msg.obj;
                        setTitleTab("Lich thi");
                        if (!lichThis.isEmpty()){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            if (sqLiteManager.getAllLThi(maSV).size()<lichThis.size()){
                                for (LichThi lichThiLop:lichThis){
                                    sqLiteManager.insertlthi(lichThiLop,maSV);
                                }
                            }
                            pullRefreshLayout.setRefreshing(false);
                            showRecircleView();
                            setRecyclerView();
                        }else{
                            showTextNull();
                            tVnull.setText("Không có lịch thi theo lớp...");
                        }
                        break;
                    case 6:
                        sv= (SinhVien) msg.obj;
                        if (sv!=null){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            sqLiteManager.insertSV(sv);
                            tvTitle.setText(sv.getTenSV());
                            tv1.setText(sv.getLopDL());
                            tv2.setText(sv.getMaSV());
                        }
                        break;
                }
            }catch (NullPointerException e){
                // neu bị null nó sẽ vào đây
                startParser();
            }
        }
    };

}