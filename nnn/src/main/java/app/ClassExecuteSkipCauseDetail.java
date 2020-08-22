
package app;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClassExecuteSkipCauseDetail {

    @SerializedName("errorName")
    @Expose
    private String errorName;
    @SerializedName("errorClassName")
    @Expose
    private List<Object> errorClassName = null;

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public List<Object> getErrorClassName() {
        return errorClassName;
    }

    public void setErrorClassName(List<Object> errorClassName) {
        this.errorClassName = errorClassName;
    }

}
