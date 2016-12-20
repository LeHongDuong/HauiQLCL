package com.lhd.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.baoyz.widget.PullRefreshLayout;
import com.ken.hauiclass.R;
import com.lhd.activity.ListActivity;
import com.lhd.activity.MainActivity;
import com.lhd.item.ItemBangKetQuaHocTap;
import com.lhd.item.KetQuaHocTap;
import com.lhd.item.SinhVien;
import com.lhd.task.ParserKetQuaHocTap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Faker on 8/25/2016.
 */

public class KetQuaHocTapFragment extends FrameFragment {
    private ArrayList<ItemBangKetQuaHocTap> bangKetQuaHocTaps;
    public void refesh() {
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MainActivity.isOnline(getContext())){
                            sqLiteManager.deleteDMon(sv.getMaSV());
                    startParser();
                }else{
                    pullRefreshLayout.setRefreshing(false);
                }

            }
        });
    }
    public void checkDatabase() {
                showProgress();

                bangKetQuaHocTaps=sqLiteManager.getBangKetQuaHocTap(sv.getMaSV());
                if (!bangKetQuaHocTaps.isEmpty()){
                    showRecircleView();
                    setRecyclerView();
                }else{
                    loadData();
                }
    }
    public void startParser() {
        ParserKetQuaHocTap ketQuaHocTapTheoMon=new ParserKetQuaHocTap(0,handler);
        ketQuaHocTapTheoMon.execute(sv.getMaSV());
    }
    public void setRecyclerView() {
        Collections.sort(bangKetQuaHocTaps, new Comparator<ItemBangKetQuaHocTap>() {
            @Override
            public int compare(ItemBangKetQuaHocTap o1, ItemBangKetQuaHocTap o2) {
                try {
                    Double.parseDouble(o2.getMaMon());
                    return o1.getMaMon().compareTo(o2.getMaMon());
                }catch (NumberFormatException e){
                    return 0;
                }
            }
        });
        Collections.reverse(bangKetQuaHocTaps);
        AdapterDiemHocTapTheoMon adapterDiemHocTapTheoMon=new AdapterDiemHocTapTheoMon(bangKetQuaHocTaps);
        recyclerView.setAdapter(adapterDiemHocTapTheoMon);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                switch (msg.arg1){
                    case 0:
                        KetQuaHocTap b;
                        b= (KetQuaHocTap) msg.obj; // lay tren internet
                        if (b.getSinhVien()!=null&&!b.getBangKetQuaHocTaps().isEmpty()){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            sqLiteManager.insertSV(b.getSinhVien());
                            bangKetQuaHocTaps=b.getBangKetQuaHocTaps();
                            if (sqLiteManager.getBangKetQuaHocTap(sv.getMaSV()).size()<bangKetQuaHocTaps.size()){
                                for (ItemBangKetQuaHocTap diemHocTapTheoMon:bangKetQuaHocTaps){
                                    sqLiteManager.insertDMon(diemHocTapTheoMon,b.getSinhVien().getMaSV());
                                }
                            }
                            pullRefreshLayout.setRefreshing(false);
                            showRecircleView();
                            setRecyclerView();
                        }
                        break;
                    case 6:
                        sv= (SinhVien) msg.obj;
                        if (sv!=null){ // nếu bên trong databse mà có dữ liệu thì ta sẽ
                            sqLiteManager.insertSV(sv);
                        }else{
                            MainActivity.showErr(getActivity());
                        }
                        break;
                }
            }catch (NullPointerException e){
                MainActivity.showErr(getActivity());
            }
        }
    };
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
        public ItemDanhSachLop onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bang_diem_thanh_phan, parent, false);
            view.setOnClickListener(this);
            ItemDanhSachLop holder = new ItemDanhSachLop(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ItemDanhSachLop holder, int position) {
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
        String[] list = new String[]{"Bảng điểm học tâp", "Kế hoạch thi theo lớp","Xem điểm "+itemBangKetQuaHocTap.getTenMon()};
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
                if (i==2){
                    String str="<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<title></title>" +
                            "<style type=\"text/css\" media=\"screen\">" +
                            "*{" +
                            "margin: auto;" +
                            "text-align: center;" +
                            "background: white;"+
                            "}" +
                            "h3{" +
                            "color: #FF4081;" +
                            "}" +
                            "p{" +
                            "color: #42A5F5;" +
                            "}" +
                            "table{" +
                            "width: 100%;" +
                            "}"+
                            "th {" +
                            "background-color: #42A5F5;" +
                            "color: white;" +
                            "padding: 5px;" +
                            "font-size: small;"+
                            "}" +
                            "td{padding: 5px;background-color: #f2f2f2;font-weight:bold;color: red;}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<h3>" +
                            itemBangKetQuaHocTap.getTenMon() +
                            "</h3>" +
                            "<p>" +
                            "("+itemBangKetQuaHocTap.getMaMon()+")<br/>" +
                            "" +
                            "</p>" +
                            "<small>"+"Bỏ "+itemBangKetQuaHocTap.getSoTietNghi()+" tiết - "+itemBangKetQuaHocTap.getDieuKien()+"</small>" +
                            "<table>" +
                            "<tr>" +
                            "<th>Điểm 1</th>" +
                            "<th>Điểm 2</th>" +
                            "<th>Điểm 3</th>" +
                            "<th>Điểm giữa kì</th>" +
                            "<th>Tổng kết</th>" +
                            "</tr>" +
                            "<tr>" +
                            "<td>"+itemBangKetQuaHocTap.getD1()+"</td>" +
                            "<td>"+itemBangKetQuaHocTap.getD2()+"</td>" +
                            "<td>"+itemBangKetQuaHocTap.getD3()+"</td>" +
                            "<td>"+itemBangKetQuaHocTap.getdGiua()+"</td>" +
                            "<td>"+itemBangKetQuaHocTap.getdTB()+"</td>" +
                            "</tr>" +
                            "</table>" +
                            "<em>Copyright  © Gà công nghiệp</em>"+
                            "</body>" +
                            "</html>";

                     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Kết quả học tập của "+sv.getTenSV());
                    WebView webView=new WebView(getActivity());
                    webView.setBackgroundColor(getResources().getColor(R.color.bg_text));
                    webView.loadDataWithBaseURL(null,str,"text/html","utf-8",null);
                    builder.setView(webView);
                    builder.setPositiveButton("IMG", null);
                    builder.setNeutralButton("SMS",null);

                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.show();
                    Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.sreenShort(view,getContext());
                        }
                    });
                    Button c = mAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    c.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.shareText(getContext(), itemBangKetQuaHocTap.getTenMon(), itemBangKetQuaHocTap.toString());
                        }
                    });
                }else {
                    Intent intent=new Intent(getActivity(),ListActivity.class);
                    intent.putExtra(KEY_OBJECT, (Serializable) itemBangKetQuaHocTap);
                    Bundle bundle=new Bundle();
                    bundle.putInt(KEY_ACTIVITY,i);
                    intent.putExtra("action",bundle);
                    getActivity().startActivityForResult(intent,1);
                    getActivity().overridePendingTransition(R.anim.left_end, R.anim.right_end);
                }
            }
        }).show();
    }
}
