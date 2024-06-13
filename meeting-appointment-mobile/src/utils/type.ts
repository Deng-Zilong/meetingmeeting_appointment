/**
 * @description: 全局配置 类型
 *
 */
import {reactive, ref} from "vue";

export const meetingState = reactive([
    {value: 0, label: '已预约'},
    {value: 1, label: '进行中'},
    {value: 2, label: '已结束'},
    {value: 3, label: '已取消'},
])