package net.center.upload_plugin.params;

import org.gradle.api.Project;

/**
 * 设置获取Git日志的参数
 */
public class GitLogParams {

    /**
     * 获取以当前时间为准的，多少天之前的Git记录，默认-1为全部，会结合数量进行获取
     */
    public int gitLogHistoryDayTime = -1;

    /**
     * Git记录的最大数量
     */
    public int gitLogMaxCount = 10;

    public static GitLogParams getGitParamsConfig(Project project) {
        GitLogParams extension = project.getExtensions().findByType(GitLogParams.class);
        return extension;
    }

}
