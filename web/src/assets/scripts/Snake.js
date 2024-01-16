import {GameObj} from "@/assets/scripts/GameObj";
import {Cell} from "@/assets/scripts/Cell";

export class Snake extends GameObj {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.hitColor = 'white';
        this.gamemap = gamemap;

        this.InitialSnakeLength = 10; // 默认蛇的初始长度
        this.StepsPerIncrease = 3; // 每隔几回合蛇尾增长一格

        this.thin = 0.8; // 蛇身占格子的比例

        this.cells = [new Cell(info.r, info.c)];
        this.nextCell = null; // 下一步目标位置

        this.speed = 5; // b per second
        this.direction = -1; // -lock1 表示无指令，0 表示上，1 表示右，2 表示下，3 表示左
        this.status = "idle"; // idle 表示静止，moving 表示移动中，hit 表示击中墙壁

        // 上右下左
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];

        this.step = 0; // 回合数

        this.eyeDirection = 0; // 0 表示上，1 表示右，2 表示下，3 表示左
        if(this.id === 0) this.eyeDirection = 1;
        if(this.id === 1) this.eyeDirection = 3;
        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1]
        ];
        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1]
        ];
        this.eye_span = 0.2; // 眼睛距离蛇头的距离
        this.eye_size = 0.06; // 眼睛的大小


    }

    start() {

    }

    setDirection(d) {
        this.direction = d;
        // console.log("directions:", this.direction);
    }

    nextStep() {
        const d = this.direction;
        this.eyeDirection = d;
        this.nextCell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);

        if(!this.gamemap.checkNotHit(this.nextCell)) {
            this.status = 'hit';
            const color = this.color;
            this.hitTimer = setInterval(() => {
                if(this.color !== this.hitColor) {
                    this.color = this.hitColor;
                } else {
                    this.color = color;
                }
                console.log('wow');
            }, 100);
            console.log("hit", "id: ", this.id);
            return;
        }

        this.direction = -1; // 清空操作
        this.status = 'moving';
        this.step ++ ;

        const k = this.cells.length;
        for(let i = k; i > 0; -- i) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1])); // 深拷贝，相当于此时蛇头存在两个Cell对象
        }


    }

    checkTailIncreasing() { // 该回合蛇尾是否增长
        if(this.step <= this.InitialSnakeLength) return true;
        return this.step % this.StepsPerIncrease === 0;
    }

    updateMove() {
        const dx = this.nextCell.x - this.cells[0].x;
        const dy = this.nextCell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy); // 两点之间目标距离
        const cos_theta = dx / distance;
        const sin_theta = dy / distance;

        if(distance < 0.1) { // 允许动画误差
            this.cells[0] = this.nextCell; // 真实目标位置
            this.nextCell = null;
            this.status = 'idle'; // 走完停下

            if(!this.checkTailIncreasing()) { // pop 蛇尾
                this.cells.pop();
            }
        } else { // 相当于只是有一个移动过程中的动画(速度调太快会发生量子隧穿效应doge)
            const move_distance = this.speed * this.timedelta; // 两帧之间移动的距离
            this.cells[0].x += move_distance * cos_theta;
            this.cells[0].y += move_distance * sin_theta;

            if (!this.checkTailIncreasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {
        if(this.status === 'moving')
            this.updateMove();
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        // 画圆
        for(const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * this.thin, 0, 2 * Math.PI);
            ctx.fill();
        }

        // 画连接圆的矩形
        for(let i = 1; i < this.cells.length; ++ i) {
            const a = this.cells[i - 1], b = this.cells[i];
            if(Math.abs(a.x - b.x) < 0.1 && Math.abs(a.y - b.y) < 0.1) continue;
            if(Math.abs(a.x - b.x) < 0.1) { // x, y 是每个圆的圆心
                ctx.fillRect((a.x - 0.5 * this.thin) * L, Math.min(a.y, b.y) * L, L * this.thin, Math.abs(a.y - b.y) * L); // 矩形在移动方向上的长度是动态变化的 即：Math.abs(a.y - b.y) * L
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.5 * this.thin) * L, Math.abs(a.x - b.x) * L, L * this.thin);
            }

        }

        // eyes
        for(let i = 0; i < 2; ++ i) {
            const eye_x = this.cells[0].x * L + this.eye_dx[this.eyeDirection][i] * L * this.eye_span * this.thin;
            const eye_y = this.cells[0].y * L + this.eye_dy[this.eyeDirection][i] * L * this.eye_span * this.thin;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * this.eye_size, 0, 2 * Math.PI);
            ctx.fillStyle = 'black';
            ctx.fill();
        }
    }

    deleteTimer() {
        clearInterval(this.hitTimer);
    }
}