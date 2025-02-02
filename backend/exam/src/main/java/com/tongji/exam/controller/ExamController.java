package com.tongji.exam.controller;

import com.tongji.exam.annotation.ApiCallMonitor;
import com.tongji.exam.entity.Exam;
import com.tongji.exam.entity.ExamRecord;
import com.tongji.exam.service.ExamService;
import com.tongji.exam.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(tags = "Exam APIs")
@RequestMapping("/exam")
public class ExamController {
    @Autowired
    private ExamService examService;

    /**
     * 获取全部考试列表
     * @return
     */
    @ApiOperation("获取全部考试的列表")
    @GetMapping("/all")
    @ApiCallMonitor(value = "获取全部考试信息",limit = 2,type = "user")
    ResultVO<List<ExamVo>> getExamAll() {
        //需要拼接前端需要的考试列表对象
        ResultVO<List<ExamVo>> resultVO;
        try {
            List<ExamVo> examVos = examService.getExamAll();
            resultVO = new ResultVO<>(0, "获取全部考试的列表成功", examVos);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取全部考试的列表失败", null);
        }
        return resultVO;
    }

    /**
     * 根据问题类别返回列表
     * @return
     */
    @GetMapping("/question/type/list")
    @ApiOperation("获取问题分类返回")
    @ApiCallMonitor(value = "获取问题分类返回")
    ResultVO<ExamQuestionTypeVo> getExamQuestionTypeList() {
        // 获取问题的分类列表
        ResultVO<ExamQuestionTypeVo> resultVO;
        try {
            ExamQuestionTypeVo examQuestionTypeVo = examService.getExamQuestionType();
            resultVO = new ResultVO<>(0, "获取问题列表成功", examQuestionTypeVo);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取问题列表失败", null);
        }
        return resultVO;
    }

    /**
     * 创建考试
     * @param examCreateVo
     * @param request
     * @return
     */
    @PostMapping("/create")
    @ApiOperation("创建考试")
    @ApiCallMonitor(value = "创建考试")
    ResultVO<Exam> createExam(@RequestBody ExamCreateVo examCreateVo, HttpServletRequest request) {
        // 从前端传参数过来，在这里完成考试的入库
        ResultVO<Exam> resultVO;
        String userId = (String) request.getAttribute("user_id");
        try {
            Exam exam = examService.create(examCreateVo, userId);
            resultVO = new ResultVO<>(0, "创建考试成功", exam);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "创建考试失败", null);
        }
        return resultVO;
    }

    /**
     *更新考试
     * @param examVo
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新考试")
    @ApiCallMonitor(value = "更新考试")
    ResultVO<Exam> updateExam(@RequestBody ExamVo examVo, HttpServletRequest request) {
        // 从前端传参数过来，在这里完成考试的入库
        ResultVO<Exam> resultVO;
        String userId = (String) request.getAttribute("user_id");
        try {
            Exam exam = examService.update(examVo, userId);
            resultVO = new ResultVO<>(0, "更新考试成功", exam);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "更新考试失败", null);
        }
        return resultVO;
    }

    /**
     * 获取考试列表，适配前端卡片列表
     * @return
     */
    @GetMapping("/card/list")
    @ApiOperation("获取考试列表，适配前端卡片列表")
    @ApiCallMonitor(value = "获取考试列表，适配前端卡片列表")
    ResultVO<List<ExamCardVo>> getExamCardList() {
        // 获取考试列表卡片
        ResultVO<List<ExamCardVo>> resultVO;
        try {
            List<ExamCardVo> examCardVoList = examService.getExamCardList();
            resultVO = new ResultVO<>(0, "获取考试列表卡片成功", examCardVoList);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取考试列表卡片失败", null);
        }
        return resultVO;
    }

    /**
     * 根据考试的id，获取考试详情
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("根据考试的id，获取考试详情")
    @ApiCallMonitor(value = "根据考试的id，获取考试详情")
    ResultVO<ExamDetailVo> getExamDetail(@PathVariable String id) {
        // 根据id获取考试详情
        ResultVO<ExamDetailVo> resultVO;
        try {
            ExamDetailVo examDetail = examService.getExamDetail(id);
            resultVO = new ResultVO<>(0, "获取考试详情成功", examDetail);
        } catch (Exception e) {
            resultVO = new ResultVO<>(-1, "获取考试详情失败", null);
        }
        return resultVO;
    }

    /**
     * 根据用户提交的答案对指定id的考试判分
     * @param examId
     * @param answersMap
     * @param request
     * @return
     */
    @PostMapping("/finish/{examId}")
    @ApiOperation("根据用户提交的答案对指定id的考试判分")
    @ApiCallMonitor(value = "提交试卷")
    ResultVO<ExamRecord> finishExam(@PathVariable String examId, @RequestBody HashMap<String, List<String>> answersMap, HttpServletRequest request) {
        ResultVO<ExamRecord> resultVO;
        try {
            // 拦截器里设置上的用户id
            String userId = (String) request.getAttribute("user_id");
            // 下面根据用户提交的信息进行判分,返回用户的得分情况
            ExamRecord examRecord = examService.judge(userId, examId, answersMap);
            resultVO = new ResultVO<>(0, "考卷提交成功", examRecord);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "考卷提交失败", null);
        }
        return resultVO;
    }

    /**
     * 获取当前用户的考试记录
     * @param request
     * @return
     */
    @GetMapping("/record/list")
    @ApiOperation("获取当前用户的考试记录")
    @ApiCallMonitor(value = "查看参加过的考试")
    ResultVO<List<ExamRecordVo>> getExamRecordList(HttpServletRequest request) {
        ResultVO<List<ExamRecordVo>> resultVO;
        try {
            // 拦截器里设置上的用户id
            String userId = (String) request.getAttribute("user_id");
            List<ExamRecordVo> examRecordVoList = examService.getExamRecordList(userId);
            resultVO = new ResultVO<>(0, "获取考试记录成功", examRecordVoList);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取考试记录失败", null);
        }
        return resultVO;
    }

    /**
     * 根据考试记录id获取考试记录详情
     * @param recordId
     * @return
     */
    @GetMapping("/record/detail/{recordId}")
    @ApiOperation("根据考试记录id获取考试记录详情")
    @ApiCallMonitor("获取参加过的考试试卷详情")
    ResultVO<RecordDetailVo> getExamRecordDetail(@PathVariable String recordId) {
        ResultVO<RecordDetailVo> resultVO;
        try {
            RecordDetailVo recordDetailVo = examService.getRecordDetail(recordId);
            resultVO = new ResultVO<>(0, "获取考试记录详情成功", recordDetailVo);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = new ResultVO<>(-1, "获取考试记录详情失败", null);
        }
        return resultVO;
    }
}
