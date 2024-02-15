import {GameObj} from "@/assets/scripts/GameObj";
import {Wall} from "@/assets/scripts/Wall";
import {Snake} from "@/assets/scripts/Snake";
export class GameMap extends GameObj {
    constructor(ctx, parent, store) {
        super();

        this.store = store;
        this.ctx = ctx; // canvas.value.getContext('2d')
        this.parent = parent; // 画布的父元素
        this.L = 0; // 一个单位长度

        // 行数列数一寄一偶可以是两条蛇不同时到达同一格子
        this.rows = store.state.pk.rows;
        this.cols = store.state.pk.cols;

        this.walls = [];
        this.createWalls();

        if(store.state.pk.role === "A") { // A 的颜色是蓝色，B 的颜色是红色
            this.snake1Color = '#5076EC';
            this.snake2Color = '#F95050';
        } else {
            this.snake1Color = '#F95050';
            this.snake2Color = '#5076EC';
        }
        this.snakes = [ // 初始化蛇
            new Snake({id: 0, color: this.snake1Color, r: this.rows - 2, c: 1}, this ),
            new Snake({id: 1, color: this.snake2Color, r: 1, c: this.cols - 2}, this )
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

    // checkNotHit(cell) {
    //     for(const wall of this.walls) {
    //         if(wall.r === cell.r && wall.c === cell.c) return false;
    //     }
    //
    //     for(const snake of this.snakes) {
    //         let k = snake.cells.length;
    //         if(!snake.checkTailIncreasing()) { // 蛇尾未增长，长度仍然为 k - 1
    //             -- k;
    //         }
    //         for(let j = 0; j < k; ++ j) {
    //             if(snake.cells[j].r === cell.r && snake.cells[j].c === cell.c) return false;
    //         }
    //     }
    //
    //     return true;
    // }

    createWalls() {
        const g = this.store.state.pk.gamemap;

        for (let r = 0; r < this.rows; ++ r) {
            for (let c = 0; c < this.cols; ++ c) {
                if(g[r][c] === 1)
                    this.walls.push(new Wall(r, c, this));
            }
        }
    }

    addListeningEvent() {
        this.ctx.canvas.focus(); // 聚焦
        this.ctx.canvas.addEventListener("keydown", e => {
            let d = -1;
            if(e.key === 'w') d = 0;
            else if(e.key === 'd') d = 1;
            else if(e.key === 's') d = 2;
            else if(e.key === 'a') d = 3;
            if(d >= 0) {
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }))
            }
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