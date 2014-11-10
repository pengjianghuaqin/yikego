package com.yikego.market.webservice;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import com.yikego.android.rom.sdk.bean.*;

import org.apache.http.HttpException;

import com.yikego.android.rom.sdk.ServiceProvider;
import com.yikego.market.model.Image2;
import com.yikego.market.utils.GlobalUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ThemeServiceAgent {

	// 测试服务器
	public static String RESOURCE_ROOT_URL = "http://121.40.182.230:8080/ykcore/";

	private static ThemeServiceAgent mInstance;
	private int nPageSize;
	private Context mContext;
	private boolean bIsLogin;
	// private String mClientId;
	private String mDeviceId;
	private String mSubsId;

	public ThemeServiceAgent(Context context) {

		this.mContext = context;
		// nRecommandAppListPageSize = Constant.RECOMMANDLIST_COUNT_PER_TIME;
		bIsLogin = false;

	}

	public static ThemeServiceAgent getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ThemeServiceAgent(context);
		}
		return mInstance;
	}

	public Object postMessageRecord(String messageRecordType, String userPhone)
			throws SocketException {
		Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.postMessageRecord(messageRecordType,
						userPhone);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
		}
	}

	public Object postUserRegister(UserRegisterInfo userRegisterInfo)
			throws SocketException {
		Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.postUserRegister(userRegisterInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public Object postUserLogin(UserLoginInfo userLoginInfo)
			throws SocketException {
		Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.postUserLogin(userLoginInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	public Object postUserCouponInfo(PostUserCouponInfo postUserCouponInfo)
			throws SocketException {
		Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.postCouponListInfo(postUserCouponInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	public Object postCouponCheck(CouponCheckInfo couponCheckInfo)
			throws SocketException {
		Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.postCouponCheck(couponCheckInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public PaginationStoreListInfo postUserLocationInfo(
			PostUserLocationInfo postUserLocationInfo) throws SocketException {
		PaginationStoreListInfo data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.getStoreList(postUserLocationInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public MarketGoodsInfoListData getMarketGoodsInfoListData(StoreId storeId)
			throws SocketException {
		MarketGoodsInfoListData data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.getProductTypeList(storeId);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public ProductListInfo getMarketGoodsInfoList(
			PostProductType postProductType) throws SocketException {
		ProductListInfo data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.getProductList(postProductType);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	public ProductListInfo getProductSearchList(
			ProductSearchInfo productSearchInfo) throws SocketException {
		ProductListInfo data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = ServiceProvider.getProductSearchList(productSearchInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	public OrderResult PostOrder(CommitOrder commitOrder)
			throws SocketException {
		OrderResult data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data = (OrderResult) ServiceProvider.postOrder(commitOrder);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public Drawable getAppIcon(String imgUrl) throws SocketException {
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			byte[] rawData = null;
			Bitmap bmp = null;
			try {
				rawData = ServiceProvider.getImage(imgUrl);
				if (rawData != null) {
					// bmp =
					// BitmapFactory.decodeByteArray(rawData, 0,
					// rawData.length);
					// return new BitmapDrawable(null, bmp);
					try {
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						BitmapFactory.decodeByteArray(rawData, 0,
								rawData.length, opts);
						// opts.inSampleSize = 2;
						opts.inJustDecodeBounds = false;
						opts.inInputShareable = true;
						opts.inPurgeable = true;
						bmp = BitmapFactory.decodeByteArray(rawData, 0,
								rawData.length, opts);

						return new BitmapDrawable(null, bmp);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						return null;
					}
				}
				return null;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				throw new SocketException();
			}
		}
	}

	public ArrayList<Drawable> getGoodsImgList(ArrayList<String> imgUrlList)
			throws SocketException {
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			byte[] rawData = null;
			Bitmap bmp = null;
			ArrayList<Drawable> imgList = new ArrayList<Drawable>();
			try {
				for (int i = 0; i < imgUrlList.size(); i++) {
					rawData = ServiceProvider.getImage(imgUrlList.get(i));
					if (rawData != null) {
						try {
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inJustDecodeBounds = true;
							BitmapFactory.decodeByteArray(rawData, 0,
									rawData.length, opts);
							// opts.inSampleSize = 2;
							opts.inJustDecodeBounds = false;
							opts.inInputShareable = true;
							opts.inPurgeable = true;
							bmp = BitmapFactory.decodeByteArray(rawData, 0,
									rawData.length, opts);
							imgList.add(new BitmapDrawable(null, bmp));
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							return null;
						}
					}
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				throw new SocketException();
			}
			return imgList;
		}
	}

	public UserOrderListInfo getUserOrder(PostUserOrderBody postUserOrderBody)
			throws SocketException {
		UserOrderListInfo userOrderListInfo = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				userOrderListInfo = ServiceProvider
						.getUserOrderList(postUserOrderBody);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return userOrderListInfo;
	}

	public UserPointListInfo getUserPoint(PostUserOrderBody postUserPointBody)
			throws SocketException {
		UserPointListInfo userPointListInfo = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				userPointListInfo = ServiceProvider
						.getUserPointList(postUserPointBody);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return userPointListInfo;
	}
    public UserOrderDetail getUserOrderDetail(PostOrderNo orderNo) throws SocketException{
        UserOrderDetail userOrderDetail = null;
        if (!GlobalUtil.checkNetworkState(mContext)) {
            throw new SocketException();
        } else {
            try {
                userOrderDetail = ServiceProvider
                        .getUserOrderDetail(orderNo);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }
        return userOrderDetail;
    }
}
