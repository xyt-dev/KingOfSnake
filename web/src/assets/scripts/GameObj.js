const GAME_OBJECTS = [];

export class GameObj {
    constructor() {
        GAME_OBJECTS.push(this);
        this.timedelta = 0;
        this.has_called_start = false
    }

    // @override
    start() { // 创建时执行一次

    }

    // @override
    update() { // 每一帧执行一次 (继承后多态)

    }
    on_destroy() { // 删除前执行

    }

    destroy() { // 销毁时执行
        this.on_destroy();

        for(let i in GAME_OBJECTS) { // 注：有先后渲染顺序，后面的 object 渲染会覆盖前面的
            if(GAME_OBJECTS[i] === this) {
                GAME_OBJECTS.splice(i, 1);
                break;
            }
        }
    }
}


let last_timestamp; // 上一次执行的时刻
const step = timestamp => {
    for (let obj of GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = (timestamp - last_timestamp) / 1000; // ms
            obj.update(); // 更新对象，多态
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}

requestAnimationFrame(step);