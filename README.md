# CommonGitHub
慢慢积累封装自己的第三方库和jar包，以后避免一直写重复代码
##一:主界面
主界面通过RecycleView显示自己以后要添加的类
##二:各个分类
1、多类型适配器，内部封装显示多种类型的适配器<br>
2、checkbox单机显示选中的所有项，以后添加不同功能<br>
3、万能适配器，这个是自定义ViewHolder和适配器，封装成一个jar包，
以后使用ListView适配器直接引用这个universal_adapter.jar包就好，
最外层放的有，直接导入就可以使用
##三：使用方式
###fyxdemoforandroid这个module就是对应调用的例子。
1、万能适配器使用例子CommBaseAdpActivity就是对应调用的例子。<br>
public class MyAdapter extends CommonAdapter<String> {
    private Context mContext;

    public MyAdapter(Context context, List<String> mDatas, int layoutId) {
        super(context, mDatas, layoutId);
        this.mContext = context;
    }

    //这里的s就是对应item的对象，可以在继承的时候换成自己对应的bean，想获取item位置直接用viewHolder.getPosition()
  
    @Override
    public void convert(CommonViewHolder viewHolder, String s) {
        viewHolder.setText(R.id.id_tv_title, s);
        viewHolder.setOnClickListener(R.id.id_tv_title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "hahaha", Toast.LENGTH_LONG).show();
            }
        });
    }
}
<br>
2、多类型操作适配器在ListViewMutiplType里面。<br>
3、新增例子。<br>
public class RecommendHistoryAdapter extends CommonAdapter<RecommendHistory>{
	private Context mContext;

	public RecommendHistoryAdapter(Context context, List<RecommendHistory> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		this.mContext = context;
	}

	@Override
	public void convert(CommonViewHolder viewHolder, RecommendHistory reommmendhistory) {
		//金额
		viewHolder.setText(R.id.txt_rec_his_money, reommmendhistory.change_fee);
		//体现时间
		viewHolder.setText(R.id.txt_rec_his_create_time, reommmendhistory.create_time);
		if(reommmendhistory.status.equals("2")){
			viewHolder.setText(R.id.txt_rec_his_state, "正在提现");//状态
			viewHolder.setBackgroundColor(R.id.txt_rec_his_state,mContext.getResources().getColor(R.color.yellow_f4ba00));
		}else if(reommmendhistory.status.equals("3")){
			viewHolder.setText(R.id.txt_rec_his_state, "提现完成");//状态
			viewHolder.setBackgroundColor(R.id.txt_rec_his_state, mContext.getResources().getColor(R.color.green));
		}
	}

}
