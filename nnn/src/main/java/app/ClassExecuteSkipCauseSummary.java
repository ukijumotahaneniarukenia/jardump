
package app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClassExecuteSkipCauseSummary {

    @SerializedName("jarFileName")
    @Expose
    private String jarFileName;
    @SerializedName("classExecuteSkipCauseDetail")
    @Expose
    private ClassExecuteSkipCauseDetail classExecuteSkipCauseDetail;

    public String getJarFileName() {
        return jarFileName;
    }

    public void setJarFileName(String jarFileName) {
        this.jarFileName = jarFileName;
    }

    public ClassExecuteSkipCauseDetail getClassExecuteSkipCauseDetail() {
        return classExecuteSkipCauseDetail;
    }

    public void setClassExecuteSkipCauseDetail(ClassExecuteSkipCauseDetail classExecuteSkipCauseDetail) {
        this.classExecuteSkipCauseDetail = classExecuteSkipCauseDetail;
    }

}
