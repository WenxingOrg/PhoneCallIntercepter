package wzhao1.phoneintercepter.intercepter;

/**
 * Created by wzhao1 on 16/5/19.
 */
public interface CallIntercepter {

    boolean interceptBlacklist(int cardSlot);

    boolean interceptStranger(int cardSlot);

    int cardSlot();
}
