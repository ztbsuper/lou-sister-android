package ztbsuper.lousysterm.events;

/**
 * Created by tbzhang on 9/12/15.
 */
public class GetItemCodeEvent {
    private String itemCode;

    public GetItemCodeEvent(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
