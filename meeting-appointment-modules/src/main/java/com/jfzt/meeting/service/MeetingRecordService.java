package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.dto.RecordQueryParameters;
import com.jfzt.meeting.entity.vo.MeetingPromptVO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.entity.vo.PeriodTimesVO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 针对表【meeting_record(会议记录表)】的数据库操作Service
 * @author zilong.deng
 * @since 2024-04-28 11:47:39
 */
public interface MeetingRecordService extends IService<MeetingRecord> {


    /**
     * 获取当天用户参与的所有会议
     * @param userId 用户id
     * @return 会议记录VO
     */
    List<MeetingRecordVO> getTodayMeetingRecord (String userId);

    /**
     * 查询今日中心会议总次数
     * @return 会议总次数
     */
    Integer getRecordNumber ();

    /**
     * 更新会议状态
     * @param meetingRecord 会议记录
     * @return 会议记录
     */
    MeetingRecord updateRecordStatus (MeetingRecord meetingRecord);

    /**
     * 更新今日所有会议状态
     */
    void updateTodayRecordStatus ();

    /**
     * 分页获取用户参与的所有会议
     * @param recordQueryParameters 查询条件
     * @return 会议记录列表
     */
    List<MeetingRecordVO> getAllRecordVoListPage (RecordQueryParameters recordQueryParameters);


    /**
     * 查询所有会议记录
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return MeetingRecordVO列表
     */
    List<MeetingRecordVO> getAllMeetingRecordVoListPage (Long pageNum, Long pageSize, Integer currentLevel);


    /**
     * 根据会议记录id删除会议(首页不展示非删除)
     * @param userId    用户id
     * @param meetingId 会议id
     * @return 删除结果
     */
    Result<String> deleteMeetingRecord (String userId, Long meetingId);

    /**
     * 根据会议记录id取消会议
     * @param userId    用户id
     * @param meetingId 会议id
     * @return 取消结果
     */
    Result<String> cancelMeetingRecord (String userId, Long meetingId);

    /**
     * 新增会议
     * @param meetingRecordDTO 会议记录DTO
     * @return 新增会议结果
     */
    Result<Objects> addMeeting (MeetingRecordDTO meetingRecordDTO);

    /**
     * 更新会议
     * @param meetingRecordDTO 会议记录DTO
     * @return 会议记录VO
     */
    Result<List<MeetingRecordVO>> updateMeeting (MeetingRecordDTO meetingRecordDTO);

    /**
     * 统计七日内各时间段预约频率
     * @return 预约频率
     */
    Result<List<PeriodTimesVO>> getTimePeriodTimes ();

    /**
     * 会议创建自动提示
     * @param userId 用户id
     * @return 最近会议信息
     */
    Result<MeetingPromptVO> prompt (String userId);

    /**
     * 导出excel
     * @param userId              用户id
     * @param meetingRecordVOList 会议室历史记录信息
     * @param response            返回respose
     * @throws IOException            io流异常
     * @throws InvalidFormatException 无效格式异常
     */
    void getRecordExport (String userId, String type, List<MeetingRecordVO> meetingRecordVOList, HttpServletResponse response, String operation) throws IOException, InvalidFormatException;

    /**
     * @param type            type = 0(excel)     type = 1(word)
     * @param operation       operation = 0（导出）， operation = 1（预览）
     * @param meetingRecordVO 会议记录
     * @return html
     */
    String excelHtml (String type, MeetingRecordVO meetingRecordVO, String operation);

    /**
     * @param type            type = 0(excel)     type = 1(word)
     * @param operation       operation = 0（导出）， operation = 1（预览）
     * @param meetingRecordVO 会议记录
     * @return html
     */
    String docxToHtml (String type, MeetingRecordVO meetingRecordVO, String operation);

    Result<List<SysDepartment>> department();
}
