/**
 * @description: 全局配置
 *
 */
import {reactive} from "vue";

export const meetingState = reactive([
    {value: 0, label: '已预约'},
    {value: 1, label: '进行中'},
    {value: 2, label: '已结束'},
    {value: 3, label: '已取消'},
])