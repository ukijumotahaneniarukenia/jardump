
package app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClassLoadSkipCauseSummary {

    @SerializedName("jarFileName")
    @Expose
    private String jarFileName;
    @SerializedName("classLoadSkipCauseDetail")
    @Expose
    private ClassLoadSkipCauseDetail classLoadSkipCauseDetail;

    public String getJarFileName() {
        return jarFileName;
    }

    public void setJarFileName(String jarFileName) {
        this.jarFileName = jarFileName;
    }

    public ClassLoadSkipCauseDetail getClassLoadSkipCauseDetail() {
        return classLoadSkipCauseDetail;
    }

    public void setClassLoadSkipCauseDetail(ClassLoadSkipCauseDetail classLoadSkipCauseDetail) {
        this.classLoadSkipCauseDetail = classLoadSkipCauseDetail;
    }

}
