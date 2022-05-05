package net.center.upload_plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;

import net.center.upload_plugin.params.SendDingParams;
import net.center.upload_plugin.params.SendFeishuParams;
import net.center.upload_plugin.model.UploadPgyParams;
import net.center.upload_plugin.params.SendWeixinGroupParams;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * 2021/9/3.
 */
public class UploadApkPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        UploadPgyParams uploadParams = project.getExtensions().create(PluginConstants.UPLOAD_PARAMS_NAME, UploadPgyParams.class);
        createParams(project);
        project.afterEvaluate(project1 -> {
            AppExtension appExtension = ((AppExtension) project1.getExtensions().findByName(PluginConstants.ANDROID_EXTENSION_NAME));
            if (appExtension == null) {
                return;
            }
            DomainObjectSet<ApplicationVariant> appVariants = ((AppExtension) project1.getExtensions().findByName(PluginConstants.ANDROID_EXTENSION_NAME)).getApplicationVariants();
            for (ApplicationVariant applicationVariant : appVariants) {
                if (applicationVariant.getBuildType() != null) {
                    dependsOnTask(applicationVariant, uploadParams, project1);
                }
            }
        });
    }

    private void createParams(Project project){
        project.getExtensions().create(PluginConstants.DING_PARAMS_NAME, SendDingParams.class);
        project.getExtensions().create(PluginConstants.FEISHU_PARAMS_NAME, SendFeishuParams.class);
        project.getExtensions().create(PluginConstants.WEIXIN_GROUP_PARAMS_NAME, SendWeixinGroupParams.class);
    }


    private void dependsOnTask(ApplicationVariant applicationVariant, UploadPgyParams uploadParams, Project project1) {
        String variantName =
                applicationVariant.getName().substring(0, 1).toUpperCase() + applicationVariant.getName().substring(1);
        if (PluginUtils.isEmpty(variantName)) {
            variantName = PluginUtils.isEmpty(uploadParams.buildTypeName) ? "Release" : uploadParams.buildTypeName;
        }
        //创建我们，上传到蒲公英的task任务
        UploadTask uploadTask = project1.getTasks()
                .create(PluginConstants.TASK_EXTENSION_NAME + variantName, UploadTask.class);
        uploadTask.init(applicationVariant, project1);

        //依赖关系 。上传依赖打包，打包依赖clean。
        applicationVariant.getAssembleProvider().get().dependsOn(project1.getTasks().findByName("clean"));
        uploadTask.dependsOn(applicationVariant.getAssembleProvider().get());
    }
}
