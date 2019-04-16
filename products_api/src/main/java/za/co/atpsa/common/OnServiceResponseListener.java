package za.co.atpsa.common;
public interface OnServiceResponseListener<T> {
    public void completed(T object);
    public void failed(ServiceException e);

}
