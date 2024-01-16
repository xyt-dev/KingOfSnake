import {GameObj} from "@/assets/scripts/GameObj";
import {Wall} from "@/assets/scripts/Wall";
import {Snake} from "@/assets/scripts/Snake";
export class GameMap extends GameObj {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx; // canvas.value.getContext('2d')
        this.parent = parent; // 画布的父元素
        this.L = 0; // 一个单位长度

        // 行数列数一寄一偶可以是两条蛇不同时到达同一格子
        this.rows = 21;
        this.cols = 20;

        this.innerWallsCount = 50; // 偶数对称
        this.walls = [];

        for(let k = 0; k < 1000; ++ k) { // 1000 次应该能够生成合法的地图
            if(this.createWalls()) break;
        }

        this.snakes = [ // 初始化蛇
            new Snake({id: 0, color: '#5076EC', r: this.rows - 2, c: 1}, this ),
            new Snake({id: 1, color: '#F95050', r: 1, c: this.cols - 2}, this )
        ]

    }

    checkReady() {
        for(const snake of this.snakes) {
            if(snake.status !== 'idle') return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    nextStep() { // 进入下一回合
        for(const snake of this.snakes) {
            snake.nextStep();
        }
    }

    checkNotHit(cell) {
        for(const wall of this.walls) {
            if(wall.r === cell.r && wall.c === cell.c) return false;
        }

        for(const snake of this.snakes) {
            let k = snake.cells.length;
            if(!snake.checkTailIncreasing()) { // 蛇尾未增长，长度仍然为 k - 1
                -- k;
            }
            for(let j = 0; j < k; ++ j) {
                if(snake.cells[j].r === cell.r && snake.cells[j].c === cell.c) return false;
            }
        }

        return true;
    }

    checkConnectivity(g, sx, sy, tx, ty) { // 检查是否连通
        if(sx === tx && sy === ty) return true;
        g[sy][sx] = true; // 注意！数组第一下标对应y轴，第二下标对应x轴
        const dx = [-1, 0, 1, 0];
        const dy = [0, 1, 0, -1];
        for(let i = 0; i < 4; ++ i){
            let x = sx + dx[i], y = sy + dy[i];
            if(!g[y][x] && this.checkConnectivity(g, x, y, tx, ty)) return true;
        }
        return false;
    }

    createWalls() {
        let g = [];
        for(let r = 0; r < this.rows; r ++) {
            g[r] = [];
            for(let c = 0; c < this.cols; c ++) {
                g[r][c] = 0;
            }
        }

        // 给四周加上墙
        for(let r = 0; r < this.rows; r ++) {
            if(r === 0 || r === this.rows - 1)
                for(let c = 0; c < this.cols; c ++)
                    g[r][c] = 1;
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        // 随机加墙
        for(let i = 0; i < this.innerWallsCount / 2;) {
            for(let j = 0; j < 1000; ++ j){
                let r = Math.round(Math.random() * (this.rows - 1));
                let c = Math.round(Math.random() * (this.cols - 1));
                if(g[r][c]) continue;
                if(r === this.rows - 2 && c === 1 || r === 1 && c === this.cols - 2) continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                ++ i;
                break;
            }
        }

        // 检查是否连通
        const copy_g = JSON.parse(JSON.stringify(g));
        if(!this.checkConnectivity(copy_g, 1, this.rows - 2, this.cols - 2, 1))
            return false;

        for(let r = 0; r < this.rows; ++ r ) {
            for (let c = 0; c < this.cols; ++c) {
                if (g[r][c] === 1)
                    this.walls.push(new Wall(r, c, this));
            }
        }

        return true;

    }


    addListeningEvent() {
        this.ctx.canvas.focus(); // 聚焦
        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if(e.key === 'w') snake0.setDirection(0);
            else if(e.key === 'd') snake0.setDirection(1);
            else if(e.key === 's') snake0.setDirection(2);
            else if(e.key === 'a') snake0.setDirection(3);
            else if(e.key === 'i') snake1.setDirection(0);
            else if(e.key === 'l') snake1.setDirection(1);
            else if(e.key === 'k') snake1.setDirection(2);
            else if(e.key === 'j') snake1.setDirection(3);
        });

        // 手机的滑动控制
        this.ctx.canvas.addEventListener("touchstart", e => {
            const touchStartX = e.touches[0].clientX;
            const touchStartY = e.touches[0].clientY;
            let lastTouchEndX, lastTouchEndY;

            this.ctx.canvas.addEventListener("touchend", e => {
                lastTouchEndX = e.changedTouches[0].clientX;
                lastTouchEndY = e.changedTouches[0].clientY;

                // 计算滑动方向
                const deltaX = lastTouchEndX - touchStartX;
                const deltaY = lastTouchEndY - touchStartY;
                const absDeltaX = Math.abs(deltaX);
                const absDeltaY = Math.abs(deltaY);

                if (absDeltaX > absDeltaY) {
                    // 水平滑动
                    if (deltaX > 0) {
                        // 向右滑动
                        touchStartX < window.innerWidth / 2 ? this.snakes[0].setDirection(1) : this.snakes[1].setDirection(1);
                    } else {
                        // 向左滑动
                        touchStartX < window.innerWidth / 2 ? this.snakes[0].setDirection(3) : this.snakes[1].setDirection(3);
                    }
                } else {
                    // 垂直滑动
                    if (deltaY > 0) {
                        // 向下滑动
                        touchStartX < window.innerWidth / 2 ? this.snakes[0].setDirection(2) : this.snakes[1].setDirection(2);
                    } else {
                        // 向上滑动
                        touchStartX < window.innerWidth / 2 ? this.snakes[0].setDirection(0) : this.snakes[1].setDirection(0);
                    }
                }
            }, { once: true });
        });
        // 手机
    }

    start() { // 创建时执行一次
        this.addListeningEvent();
    }

    updateSize() { // 每一帧执行一次
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows); // clientWidth: 内部宽度，包括 padding，但不包括垂直滚动条、border 和 margin。
        this.L = Math.round(this.L); // 取整，去除奇怪的边界线，同时也会使地图和 parent 的边界不完全对齐
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() { // 每一帧执行一次
        this.updateSize();
        if(this.checkReady()) { // 蛇都就绪则进入下一回合
            console.log("Ready");
            this.nextStep();
        }
        this.render() // 渲染地图草地
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";

        for(let r = 0; r < this.rows; r++) {
            for(let c = 0; c < this.cols; c++) {
                this.ctx.fillStyle = (r + c) % 2 ? color_even : color_odd;
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L); // x, y, width, height
            }
        }
    }

    deleteTimer() {
        for(const snake of this.snakes) {
            snake.deleteTimer();
        }
    }
}